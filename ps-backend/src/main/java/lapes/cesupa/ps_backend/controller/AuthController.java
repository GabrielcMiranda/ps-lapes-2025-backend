package lapes.cesupa.ps_backend.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lapes.cesupa.ps_backend.dto.CreateUser;
import lapes.cesupa.ps_backend.dto.LoginRequest;
import lapes.cesupa.ps_backend.dto.LoginResponse;
import lapes.cesupa.ps_backend.dto.ProfileResponse;
import lapes.cesupa.ps_backend.dto.RefreshRequest;

import lapes.cesupa.ps_backend.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){

        var response = authService.login(loginRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest refreshRequest) {

        var response = authService.refresh(refreshRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> newUser(@RequestBody @Valid CreateUser dto) {
        
        authService.createUser(dto);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> profile(@AuthenticationPrincipal Jwt jwt) {

        var profile = authService.profile(jwt.getSubject());

        return ResponseEntity.ok(profile);
    }
    
    
    
}
