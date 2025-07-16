package lapes.cesupa.ps_backend.service;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lapes.cesupa.ps_backend.model.Role;
import lapes.cesupa.ps_backend.model.User;

@Service
public class TokenService {

    private final JwtDecoder jwtDecoder;

    private final JwtEncoder jwtEncoder;
    public static final Long ACCESS_EXPIRATION = 36000L;
    public static final Long REFRESH_EXPIRATION = 604800L;

    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
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

    public Jwt validateRefreshToken(String refreshToken){
        Jwt decodedRefreshToken;
        try{
            decodedRefreshToken = jwtDecoder.decode(refreshToken);
        }catch(JwtException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid refresh token");
        }

        var type = decodedRefreshToken.getClaimAsString("type");
        if(!"refresh".equals(type)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid refresh token type");
        }

        return decodedRefreshToken;
    }
}
