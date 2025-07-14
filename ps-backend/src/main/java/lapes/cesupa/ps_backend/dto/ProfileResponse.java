package lapes.cesupa.ps_backend.dto;

import java.util.List;
import java.util.UUID;

import lapes.cesupa.ps_backend.model.Role;

public record ProfileResponse(UUID id, String username, String email, List<String> roles) {

}
