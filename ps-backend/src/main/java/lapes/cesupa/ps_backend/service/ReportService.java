package lapes.cesupa.ps_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lapes.cesupa.ps_backend.dto.CustomerAnalyticsResponse;
import lapes.cesupa.ps_backend.dto.PopularItemResponse;
import lapes.cesupa.ps_backend.dto.SalesReportResponse;
import lapes.cesupa.ps_backend.model.Order;
import lapes.cesupa.ps_backend.model.Order.OrderStatus;
import lapes.cesupa.ps_backend.model.OrderItem;
import lapes.cesupa.ps_backend.model.Role;
import lapes.cesupa.ps_backend.model.User;
import lapes.cesupa.ps_backend.repository.OrderRepository;
import lapes.cesupa.ps_backend.repository.RoleRepository;
import lapes.cesupa.ps_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final DeliveryService deliveryService;

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public SalesReportResponse getSalesReport(LocalDate startDate, LocalDate endDate){
        var orders = orderRepository.findAll().stream()
        .filter(order -> !order.getOrderStatus().equals(OrderStatus.CANCELLED))
        .filter(order -> {
            LocalDate orderDate = order.getCreatedAt().toLocalDate(); 
            return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
        })
        .toList();

        long totalOrders = orders.size();
        long totalRevenue = orders.stream()
            .mapToLong(order -> getTotalInCents(order)) 
            .sum();

        double averageTicket = totalOrders > 0 ? (double) totalRevenue / totalOrders : 0.0;

        return new SalesReportResponse(totalOrders, totalRevenue, averageTicket);
    }

    public List<PopularItemResponse> getPopularItemsReport() {
        return orderRepository.findAll().stream()
            .filter(order -> order.getOrderStatus() == OrderStatus.DELIVERED)
            .flatMap(order -> order.getItems().stream())
            .collect(Collectors.groupingBy(
                orderItem -> orderItem.getItem(),
                Collectors.summingInt(OrderItem::getQuantity)
            ))
            .entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .map(entry -> new PopularItemResponse(
                entry.getKey().getId(),
                entry.getKey().getName(),
                entry.getValue()
            ))
            .toList();
    }

    public CustomerAnalyticsResponse getCustomerAnalytics(LocalDate startDate, LocalDate endDate) {

        Role customerRole = roleRepository.findById(4L)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found"));

        List<User> allCustomers = userRepository.findAll().stream()
            .filter(user -> user.getRoles().contains(customerRole))
            .toList();

        long newCustomers = allCustomers.stream()
            .filter(c -> {
                LocalDate createdAt = c.getCreated_at().toLocalDate();
                return !createdAt.isBefore(startDate) && !createdAt.isAfter(endDate);
            })
            .count();

        Map<UUID, Long> orderCountByCustomer = orderRepository.findAll().stream()
            .filter(order -> order.getReceiver() != null)
            .collect(Collectors.groupingBy(
                order -> order.getReceiver().getId(),
                Collectors.counting()
            ));

        double avgOrders = orderCountByCustomer.values().stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);

        long customersWithReorders = orderCountByCustomer.values().stream()
            .filter(count -> count > 1)
            .count();

        double reorderRate = allCustomers.isEmpty() ? 0.0 :
            ((double) customersWithReorders / allCustomers.size()) * 100;

        return new CustomerAnalyticsResponse(newCustomers, avgOrders, reorderRate);
    }


    private Long getTotalInCents(Order order) {
        if (order.getItems() == null) return 0L;

        var total = order.getItems().stream()
            .mapToLong(orderItem -> {
                Integer price = orderItem.getItem().getPriceInCents();
                int quantity = orderItem.getQuantity();
                return (long) price * quantity;
            })
            .sum();

        String addressStr;
        var taxes = 0L;
        if(order.getAddress() != null){
            addressStr = order.getAddress().getStreet() + ", " + order.getAddress().getNumber();
            try{
                taxes = (int) deliveryService.calculate(addressStr).tax_value();
            }catch(Exception e){
                taxes = 100000L;
            }
        }
        return total + taxes;  
    }
}
