package lapes.cesupa.ps_backend.dto;

public record LoginResponse(String accessToken, Long accessExpiration, String refreshToken, Long refreshExpiration) {

}
