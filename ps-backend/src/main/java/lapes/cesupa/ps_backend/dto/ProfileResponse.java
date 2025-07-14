package lapes.cesupa.ps_backend.dto;

import java.util.List;
import java.util.UUID;

public record ProfileResponse(UUID id, String username, String email, List<String> roles) {

}
