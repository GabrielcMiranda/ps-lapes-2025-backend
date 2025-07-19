package lapes.cesupa.ps_backend.model;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lapes.cesupa.ps_backend.dto.LoginRequest;
import lombok.Data;

@Entity
@Data
@Table(name="users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    @Column(nullable = false)
    private Set<Role> roles;

    public boolean isLoginCorrect(LoginRequest loginRequest, PasswordEncoder passwordEncoder){
            return passwordEncoder.matches(loginRequest.password(), this.password);
        }
}
