package lapes.cesupa.ps_backend.service;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import lapes.cesupa.ps_backend.model.Role;
import lapes.cesupa.ps_backend.model.User;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    public static final Long ACCESS_EXPIRATION = 36000L;
    public static final Long REFRESH_EXPIRATION = 604800L;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateAccessToken(User user){
        var now = Instant.now();
        
        var scopes = user.getRoles()
                            .stream()
                            .map(Role::getName)
                            .collect(Collectors.joining(" "));

        var accessClaims = JwtClaimsSet.builder()
                .issuer("ps_backend")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ACCESS_EXPIRATION))
                .claim("scope", scopes)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();
    }

    public String generateRefreshToken(User user){
        var now = Instant.now();

        var refreshClaims = JwtClaimsSet.builder()
                .issuer("ps_backend")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(REFRESH_EXPIRATION))
                .claim("type", "refresh")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();
    }
}
