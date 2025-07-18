package lapes.cesupa.ps_backend.dto;

import java.util.List;

public record ListItemResponse( Long id,
    String name,
    String description,
    Integer priceInCents,
    Integer estimatedPrepTime,
    List<String> imageUrls,
    String extraAttributes) {

}
