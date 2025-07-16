package lapes.cesupa.ps_backend.dto;

import java.util.List;

public record ProfileResponse(String username, String email, List<String> roles, String Phone) {

}
