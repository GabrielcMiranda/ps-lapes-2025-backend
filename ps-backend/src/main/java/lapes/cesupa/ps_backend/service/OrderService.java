package lapes.cesupa.ps_backend.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import lapes.cesupa.ps_backend.dto.CreateOrderRequest;
import lapes.cesupa.ps_backend.dto.ItemResponse;
import lapes.cesupa.ps_backend.dto.OrderItemRequest;
import lapes.cesupa.ps_backend.dto.OrderResponse;
import lapes.cesupa.ps_backend.dto.TrackOrderResponse;
import lapes.cesupa.ps_backend.model.Address;
import lapes.cesupa.ps_backend.model.Order;
import lapes.cesupa.ps_backend.model.OrderItem;
import lapes.cesupa.ps_backend.model.Role;
import lapes.cesupa.ps_backend.repository.AddressRepository;
import lapes.cesupa.ps_backend.model.Order.OrderStatus;
import lapes.cesupa.ps_backend.model.Order.OrderType;
import lapes.cesupa.ps_backend.repository.OrderRepository;
import lapes.cesupa.ps_backend.repository.RoleRepository;
import lapes.cesupa.ps_backend.specification.OrderSpecifications;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AddressRepository addressRepository;

    private final ItemService itemService;

    private final AuthService authService;

    private final OrderRepository orderRepository;

    private final RoleRepository roleRepository;

    @Transactional
    public OrderResponse create(CreateOrderRequest dto, String userId){
        var user = authService.validateUserId(userId);
        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemRequest i: dto.items()){

            var item = itemService.validateItemId(i.id());

            int priceSnapshot = item.getPriceInCents();

            var orderItem = new OrderItem();

            orderItem.setItem(item);;
            orderItem.setQuantity(i.quantity());
            orderItem.setPrice(priceSnapshot);
            orderItems.add(orderItem);
        }

        var now = LocalDateTime.now();

        Order order = new Order();
        order.setOrderType(dto.orderType());
        order.setReceiver(user);
        order.setNotes(dto.notes());
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setItems(orderItems);

        if(dto.orderType() == OrderType.DELIVERY && dto.deliveryAddress() != null){
            Address address = new Address();
            address.setStreet(dto.deliveryAddress().street());
            address.setNumber(dto.deliveryAddress().number());
            address.setNeighbourhood(dto.deliveryAddress().neighborhood());
            address.setCity(dto.deliveryAddress().city());
            address.setState(dto.deliveryAddress().state());
            address.setZipCode(dto.deliveryAddress().zipCode());
            address.setComplement(dto.deliveryAddress().complement());
            addressRepository.save(address);
            order.setAddress(address);
        }else{
            var takeawayAddress = addressRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Takeaway address not configured"));

            order.setAddress(takeawayAddress);
        }

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        var savedOrder = orderRepository.save(order);

        List<ItemResponse> itemResponses = savedOrder.getItems().stream()
            .map(item -> new ItemResponse(
                    item.getItem().getId(),
                    item.getItem().getName(),
                    item.getQuantity(),
                    item.getPrice()
            ))
            .toList();

        String addressStr = (savedOrder.getAddress() != null)
            ? savedOrder.getAddress().getStreet() + ", " + savedOrder.getAddress().getNumber()
            : null;

        return new OrderResponse(
            savedOrder.getId(),
            savedOrder.getOrderType(),
            savedOrder.getReceiver().getUsername(),
            addressStr,
            itemResponses,
            savedOrder.getNotes(),
            savedOrder.getOrderStatus()
        );
    }

    public List<OrderResponse> listAll(String id, OrderStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        var costumerRole = roleRepository.findById(4L)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found"));
        var user = authService.validateUserId(id);
        var spec = Specification.<Order>where(null)
                .and(OrderSpecifications.hasStatus(status))
                .and(OrderSpecifications.createdAfter(startDate))
                .and(OrderSpecifications.createdBefore(endDate));
        
        var orders = orderRepository.findAll(spec).stream();
        
        if(user.getRoles().contains(costumerRole)){
            orders = orders.filter(order -> order.getReceiver().equals(user));
        }

        return orders.map(order -> {
            List<ItemResponse> items = order.getItems().stream().map(item -> new ItemResponse(
                    item.getItem().getId(),
                    item.getItem().getName(),
                    item.getQuantity(),
                    item.getPrice())
                ).toList();

    String addressStr = order.getAddress() != null
            ? order.getAddress().getStreet() + ", " + order.getAddress().getNumber()
            : null;

    return new OrderResponse(
            order.getId(),
            order.getOrderType(),
            order.getReceiver().getUsername(),
            addressStr,
            items,
            order.getNotes(),
            order.getOrderStatus()
            );
        }).toList();
    }

    public OrderResponse get(String userId, Long orderId){
        var user = authService.validateUserId(userId);
        var userOrder = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));

        if(!userOrder.getReceiver().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user does not have permission for that order");
        }

        List<ItemResponse> items = userOrder.getItems().stream().map(item -> new ItemResponse(
                    item.getItem().getId(),
                    item.getItem().getName(),
                    item.getQuantity(),
                    item.getPrice())
                ).toList();

        String addressStr = userOrder.getAddress() != null
            ? userOrder.getAddress().getStreet() + ", " + userOrder.getAddress().getNumber()
            : null;

        return new OrderResponse(userOrder.getId(),
            userOrder.getOrderType(),
            userOrder.getReceiver().getUsername(),
            addressStr,
            items,
            userOrder.getNotes(),
            userOrder.getOrderStatus());
    }

    public TrackOrderResponse track(String userId, Long orderId){
        var user = authService.validateUserId(userId);
        var userOrder = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));

        if(!userOrder.getReceiver().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user does not have permission for that order");
        }

        return new TrackOrderResponse(userOrder.getId(), userOrder.getOrderStatus(), userOrder.getUpdatedAt().atZone(ZoneOffset.UTC).toInstant());
    }

    @Transactional
    public Order confirm(String userId, Long orderId){
        var user = authService.validateUserId(userId);
        var userOrder = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));

        if(!userOrder.getReceiver().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user does not have permission for that order");
        }

        userOrder.setOrderStatus(OrderStatus.DELIVERED);
        return orderRepository.save(userOrder);
    }
}
