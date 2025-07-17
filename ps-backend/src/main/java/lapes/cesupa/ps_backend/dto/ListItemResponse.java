package lapes.cesupa.ps_backend.dto;

public record ListItemResponse( Long id,
    String name,
    String description,
    Integer priceInCents,
    Boolean available) {

}
