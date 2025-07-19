package lapes.cesupa.ps_backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lapes.cesupa.ps_backend.dto.CreateUser;
import lapes.cesupa.ps_backend.dto.LoginRequest;
import lapes.cesupa.ps_backend.dto.LoginResponse;
import lapes.cesupa.ps_backend.dto.ProfileResponse;
import lapes.cesupa.ps_backend.dto.RefreshRequest;
import lapes.cesupa.ps_backend.model.Role;
import lapes.cesupa.ps_backend.model.User;
import lapes.cesupa.ps_backend.repository.RoleRepository;
import lapes.cesupa.ps_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final TokenService tokenService;

    public LoginResponse login(LoginRequest dto){
         var user = validateLogin(dto);
        
        var loggedUser = user.get();

        var jwtAccessValue = tokenService.generateAccessToken(loggedUser);
        var jwtRefreshValue = tokenService.generateRefreshToken(loggedUser);

        return new LoginResponse(jwtAccessValue, TokenService.ACCESS_EXPIRATION, jwtRefreshValue, TokenService.REFRESH_EXPIRATION);
    }

    public LoginResponse refresh(RefreshRequest dto){
        var decodedRefreshToken = tokenService.validateRefreshToken(dto.refreshToken());

        var userId = decodedRefreshToken.getSubject();
        var user = validateUserId(userId);

        var jwtAccessValue = tokenService.generateAccessToken(user);
        var jwtRefreshValue = tokenService.generateRefreshToken(user);

        return new LoginResponse(jwtAccessValue, TokenService.ACCESS_EXPIRATION, jwtRefreshValue, TokenService.REFRESH_EXPIRATION);
    }

    @Transactional
    public User createUser(CreateUser dto){
        var costumerRole = roleRepository.findById(4L)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found"));

        postUserValidation(dto.username(),dto.email());

        var now = LocalDateTime.now();
        var user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        user.setPassword(bCryptPasswordEncoder.encode(dto.password()));
        user.setRoles(Set.of(costumerRole));
        user.setCreated_at(now);
        user.setUpdated_at(now);

        return userRepository.save(user);
    }

    public ProfileResponse profile(String userId){
        var user = validateUserId(userId);

        var roles = user.getRoles().stream().map(Role::getName).toList();

        return new ProfileResponse(user.getUsername(),user.getEmail(), roles, user.getPhone());
    }

    public User validateUserId(String id){
        return userRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
    }

     public User validateUserUUID(UUID id){
        return userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
    }

    private Optional<User> validateLogin(LoginRequest dto){
        var user = userRepository.findByUsername(dto.login())
        .or(() -> userRepository.findByEmail(dto.login()));
        
        if(user.isEmpty() || !user.get().isLoginCorrect(dto, bCryptPasswordEncoder)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid user or password");
        }
        return user;
    }

    private void postUserValidation(String username, String email){
        boolean exists = userRepository.existsByUsername(username) || userRepository.existsByEmail(email);
        
        if(exists){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "username or email already exist");
        }
    }
}
