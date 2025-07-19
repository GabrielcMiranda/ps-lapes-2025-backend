package lapes.cesupa.ps_backend.controller;

import org.springframework.web.bind.annotation.RestController;

import lapes.cesupa.ps_backend.dto.CreateOrderRequest;
import lapes.cesupa.ps_backend.dto.OrderResponse;
import lapes.cesupa.ps_backend.model.Order.OrderStatus;
import lapes.cesupa.ps_backend.service.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_COSTUMER')")
    public ResponseEntity<Void> create(@RequestBody CreateOrderRequest dto, @AuthenticationPrincipal Jwt jwt) {
        orderService.create(dto, jwt.getSubject());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_COSTUMER')")
    public ResponseEntity<List<OrderResponse>> listOrders(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam(required = false) OrderStatus status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
        ){
        var orders = orderService.listAll(jwt.getSubject(), status, startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_COSTUMER')")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt) {
        var order = orderService.get(jwt.getSubject(), id);
        return ResponseEntity.ok(order);
    }
    
    
    
    
}
