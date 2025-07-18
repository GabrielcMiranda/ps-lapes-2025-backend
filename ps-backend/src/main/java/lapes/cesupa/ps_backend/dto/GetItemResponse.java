package lapes.cesupa.ps_backend.dto;

import java.util.List;

public record GetItemResponse(String name, String description, List<String>ImageUrl, boolean isAvailable) {

}
