package lapes.cesupa.ps_backend.service;

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

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder, TokenService tokenService){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenService = tokenService;
    }

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

        var loggedUser = user.get();

        var jwtAccessValue = tokenService.generateAccessToken(loggedUser);
        var jwtRefreshValue = tokenService.generateRefreshToken(loggedUser);

        return new LoginResponse(jwtAccessValue, TokenService.ACCESS_EXPIRATION, jwtRefreshValue, TokenService.REFRESH_EXPIRATION);
    }

    @Transactional
    public User createUser(CreateUser dto){
        var costumerRole = roleRepository.findByName(Role.Values.COSTUMER.name());
        postUsernameValidation(dto.username());

        var user = new User();
        user.setUsername(dto.username());
        user.setPassword(bCryptPasswordEncoder.encode(dto.password()));
        user.setRoles(Set.of(costumerRole));

        return userRepository.save(user);
    }

    public ProfileResponse profile(String userId){
        var user = validateUserId(userId).get();

        var roles = user.getRoles().stream().map(Role::getName).toList();

        return new ProfileResponse(user.getId(), user.getUsername(),user.getEmail(), roles);
    }

    private Optional<User> validateUserId(String id){
        var user = userRepository.findById(UUID.fromString(id));
        
        if(user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }
        return user;
    }

    private Optional<User> validateLogin(LoginRequest dto){
        var user = userRepository.findByUsername(dto.username());
        
        if(user.isEmpty() || !user.get().isLoginCorrect(dto, bCryptPasswordEncoder)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid user or password");
        }
        return user;
    }

    private Optional<User> postUsernameValidation(String username){
        var user = userRepository.findByUsername(username);
        
        if(user.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "username already exists");
        }
        return user;
    }
}
