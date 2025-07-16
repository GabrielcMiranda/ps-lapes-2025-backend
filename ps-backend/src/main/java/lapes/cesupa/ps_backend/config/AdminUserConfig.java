package lapes.cesupa.ps_backend.config;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.transaction.Transactional;
import lapes.cesupa.ps_backend.model.Role;
import lapes.cesupa.ps_backend.model.User;
import lapes.cesupa.ps_backend.repository.RoleRepository;
import lapes.cesupa.ps_backend.repository.UserRepository;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;

    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        addAdmin();
        addKitchen();
    }

    private void addAdmin(){

        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

        var userAdmin = userRepository.findByUsername("admin");

        userAdmin.ifPresentOrElse(
            user -> System.out.println("admin is already logged"),
            () -> {
                var now = LocalDateTime.now();
                var user = new User();
                user.setUsername("admin");
                user.setEmail("gabrielcostademiranda@gmail.com");
                user.setPhone("5591992981511");
                user.setPassword(passwordEncoder.encode("123"));
                user.setRoles(Set.of(roleAdmin));
                user.setCreated_at(now);
                user.setUpdated_at(now);
                userRepository.save(user);
            }
        );
    }

    private void addKitchen(){

        var roleKitchen = roleRepository.findByName(Role.Values.KITCHEN.name());

        var userKitchen = userRepository.findByUsername("kitchen");

        userKitchen.ifPresentOrElse(
            user -> System.out.println("kitchen is already logged"),
            () -> {
                var now = LocalDateTime.now();
                var user = new User();
                user.setUsername("kitchen");
                user.setEmail("gabrielcostademiranda2@gmail.com");
                user.setPhone("5591984145141");
                user.setPassword(passwordEncoder.encode("123"));
                user.setRoles(Set.of(roleKitchen));
                user.setCreated_at(now);
                user.setUpdated_at(now);
                userRepository.save(user);
            }
            );
    }
}
