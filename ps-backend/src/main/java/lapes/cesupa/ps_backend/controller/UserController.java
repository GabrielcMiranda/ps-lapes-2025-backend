package lapes.cesupa.ps_backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import lapes.cesupa.ps_backend.dto.CreateUser;
import lapes.cesupa.ps_backend.dto.ProfileResponse;
import lapes.cesupa.ps_backend.model.Role;
import lapes.cesupa.ps_backend.model.User;
import lapes.cesupa.ps_backend.repository.RoleRepository;
import lapes.cesupa.ps_backend.repository.UserRepository;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class UserController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
 
    
    public UserController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    @PostMapping("auth/register")
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

    @GetMapping("auth/profile")
    public ResponseEntity<ProfileResponse> profile(@AuthenticationPrincipal Jwt jwt) {
        var userId = jwt.getSubject();
        var user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new BadCredentialsException("user not found"));

        var roles = user.getRoles().stream().map(Role::getName).toList();

        var profile = new ProfileResponse(user.getId(), user.getUsername(),user.getEmail(), roles);

        return ResponseEntity.ok(profile);
    }
    
    
}
