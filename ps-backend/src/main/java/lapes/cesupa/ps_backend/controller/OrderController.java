package lapes.cesupa.ps_backend.controller;

import org.springframework.web.bind.annotation.RestController;

import lapes.cesupa.ps_backend.dto.CreateOrderRequest;
import lapes.cesupa.ps_backend.service.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    
    
}
