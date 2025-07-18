package lapes.cesupa.ps_backend.dto;

import java.util.List;

import lapes.cesupa.ps_backend.model.Order.OrderStatus;
import lapes.cesupa.ps_backend.model.Order.OrderType;

public record OrderResponse(
    Long id,
    OrderType orderType,
    String receiver,
    String deliveryAddress,
    List<ItemResponse> items,
    String notes,
    OrderStatus status
) {

}
