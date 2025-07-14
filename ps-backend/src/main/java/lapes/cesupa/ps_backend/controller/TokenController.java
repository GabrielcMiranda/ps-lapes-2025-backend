package lapes.cesupa.ps_backend.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.RestController;

import lapes.cesupa.ps_backend.dto.LoginRequest;
import lapes.cesupa.ps_backend.dto.LoginResponse;
import lapes.cesupa.ps_backend.dto.RefreshRequest;
import lapes.cesupa.ps_backend.repository.UserRepository;
import lapes.cesupa.ps_backend.service.TokenService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class TokenController {
    
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;

    public TokenController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtDecoder jwtDecoder, TokenService tokenService){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtDecoder = jwtDecoder;
        this.tokenService = tokenService;
    }

    @PostMapping("auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){

        var user = userRepository.findByUsername(loginRequest.username());
        
        if(user.isEmpty() || !user.get().isLoginCorrect(loginRequest, bCryptPasswordEncoder)){
            throw new BadCredentialsException("invalid user or password");
        }
        var loggedUser = user.get();

        var jwtAccessValue = tokenService.generateAccessToken(loggedUser);
        var jwtRefreshValue = tokenService.generateRefreshToken(loggedUser);

        return ResponseEntity.ok(new LoginResponse(jwtAccessValue, TokenService.ACCESS_EXPIRATION, jwtRefreshValue, TokenService.REFRESH_EXPIRATION));
    }

    @PostMapping("auth/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest refreshRequest) {

        Jwt decodedRefreshToken;
        try{
            decodedRefreshToken = jwtDecoder.decode(refreshRequest.refreshToken());
        }catch(JwtException e){
            throw new BadCredentialsException("invalid refresh token");
        }

        var type = decodedRefreshToken.getClaimAsString("type");
        if(!"refresh".equals(type)){
            throw new BadCredentialsException("invalid refresh token type");
        }

        var userId = decodedRefreshToken.getSubject();
        var user = userRepository.findById(UUID.fromString(userId));
        if(user.isEmpty()){
            throw new BadCredentialsException("user not found");
        }
        var loggedUser = user.get();

        var jwtAccessValue = tokenService.generateAccessToken(loggedUser);
        var jwtRefreshValue = tokenService.generateRefreshToken(loggedUser);

       
        return ResponseEntity.ok(new LoginResponse(jwtAccessValue, TokenService.ACCESS_EXPIRATION, jwtRefreshValue, TokenService.REFRESH_EXPIRATION));
    }
    
    
}
