package lapes.cesupa.ps_backend.dto;

import java.time.Instant;

import lapes.cesupa.ps_backend.model.Order.OrderStatus;

public record TrackOrderResponse(Long id, OrderStatus status, Instant lastUpdate) {

}
