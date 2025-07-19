package lapes.cesupa.ps_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lapes.cesupa.ps_backend.config.TakeawayAddressProperties;
import lapes.cesupa.ps_backend.dto.CreateOrderRequest;
import lapes.cesupa.ps_backend.dto.ItemResponse;
import lapes.cesupa.ps_backend.dto.OrderItemRequest;
import lapes.cesupa.ps_backend.dto.OrderResponse;
import lapes.cesupa.ps_backend.model.Address;
import lapes.cesupa.ps_backend.model.Order;
import lapes.cesupa.ps_backend.model.OrderItem;
import lapes.cesupa.ps_backend.repository.AddressRepository;
import lapes.cesupa.ps_backend.model.Order.OrderStatus;
import lapes.cesupa.ps_backend.model.Order.OrderType;
import lapes.cesupa.ps_backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AddressRepository addressRepository;

    private final ItemService itemService;

    private final AuthService authService;

    private final OrderRepository orderRepository;

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
        String receiver = user.get().getUsername();
        if(dto.receiver()!= null && dto.receiver().isBlank()){
            receiver = dto.receiver();
        }

        Order order = new Order();
        order.setOrderType(dto.orderType());
        order.setReceiver(receiver);
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
            var takeawayAddress = addressRepository.findById(9L)
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
            savedOrder.getReceiver(),
            addressStr,
            itemResponses,
            savedOrder.getNotes(),
            savedOrder.getOrderStatus()
        );
    }

}
