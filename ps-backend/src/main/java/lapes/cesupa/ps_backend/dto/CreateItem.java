package lapes.cesupa.ps_backend.dto;

import java.util.Set;

public record CreateItem(String name, String description, Integer priceInCents, Set<Long> categoryIds, Integer estimatedPrepTime, boolean isAvailable) {

}
