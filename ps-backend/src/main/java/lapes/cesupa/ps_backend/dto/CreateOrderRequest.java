package lapes.cesupa.ps_backend.dto;

import java.util.List;

import lapes.cesupa.ps_backend.model.Order.OrderType;

public record CreateOrderRequest(

    OrderType orderType,
    DeliveryAddressRequest deliveryAddress,
    List<OrderItemRequest> items,
    String notes
) {}
