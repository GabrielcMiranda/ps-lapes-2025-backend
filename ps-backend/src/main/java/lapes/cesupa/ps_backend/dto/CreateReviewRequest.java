package lapes.cesupa.ps_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CreateReviewRequest(Long itemId, @Min(1)@Max(5) Integer rating, String comment) {

}
