package lapes.cesupa.ps_backend.config;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;
import lapes.cesupa.ps_backend.model.Address;
import lapes.cesupa.ps_backend.model.Role;
import lapes.cesupa.ps_backend.model.User;
import lapes.cesupa.ps_backend.repository.AddressRepository;
import lapes.cesupa.ps_backend.repository.RoleRepository;
import lapes.cesupa.ps_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final TakeawayAddressProperties takeawayAddress;

    private final AddressRepository addressRepository;

    @Override
    public void run(String... args) throws Exception {
        checkTakeawayAddressProperties();
        addAdmin();
        addKitchen();
        addTakeawayAddress();
    }

    private void addAdmin(){

        var roleAdmin = roleRepository.findById(1L)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found"));

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

        var roleKitchen = roleRepository.findById(2L)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found"));

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

    private void addTakeawayAddress(){
        var address = addressRepository.findById(1L);

        if (address.isEmpty()) {
            var takeAddress = takeawayAddress.toAddress();
            Address saved = addressRepository.save(takeAddress);
            System.out.println("takeaway address created: id=" + saved.getId());
            System.out.println(saved.toString());
        } else {
            System.out.println("takeaway address already registered");
            System.out.println(address.toString());
        }
    }

    @PostConstruct
    public void checkTakeawayAddressProperties() {
        System.out.println("[DEBUG] TakeawayAddressProperties bean foi carregado? " + takeawayAddress);
        System.out.println("[DEBUG] Street: " + takeawayAddress.getStreet());
    }
}
