package lapes.cesupa.ps_backend.dto;

public record ResetPasswordRequest(String token, String newPassword) {

}
