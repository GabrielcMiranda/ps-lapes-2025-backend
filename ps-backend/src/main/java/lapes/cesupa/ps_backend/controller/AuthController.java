package lapes.cesupa.ps_backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import lapes.cesupa.ps_backend.dto.CreateUser;
import lapes.cesupa.ps_backend.dto.LoginRequest;
import lapes.cesupa.ps_backend.dto.LoginResponse;
import lapes.cesupa.ps_backend.dto.ProfileResponse;
import lapes.cesupa.ps_backend.dto.RefreshRequest;
import lapes.cesupa.ps_backend.model.Role;
import lapes.cesupa.ps_backend.model.User;
import lapes.cesupa.ps_backend.repository.RoleRepository;
import lapes.cesupa.ps_backend.repository.UserRepository;
import lapes.cesupa.ps_backend.service.TokenService;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/auth")
public class AuthController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtDecoder jwtDecoder;
    private final TokenService tokenService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    public AuthController(BCryptPasswordEncoder bCryptPasswordEncoder, JwtDecoder jwtDecoder, TokenService tokenService,
            UserRepository userRepository, RoleRepository roleRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtDecoder = jwtDecoder;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/login")
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

    @PostMapping("/refresh")
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

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<Void> newUser(@RequestBody CreateUser dto) {
        var costumerRole = roleRepository.findByName(Role.Values.COSTUMER.name());
        var userFromDb = userRepository.findByUsername(dto.username());

        if (userFromDb.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var user = new User();
        user.setUsername(dto.username());
        user.setPassword(bCryptPasswordEncoder.encode(dto.password()));
        user.setRoles(Set.of(costumerRole));
        userRepository.save(user);

        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> profile(@AuthenticationPrincipal Jwt jwt) {
        var userId = jwt.getSubject();
        var user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new BadCredentialsException("user not found"));

        var roles = user.getRoles().stream().map(Role::getName).toList();

        var profile = new ProfileResponse(user.getId(), user.getUsername(),user.getEmail(), roles);

        return ResponseEntity.ok(profile);
    }
    
    
    
}
