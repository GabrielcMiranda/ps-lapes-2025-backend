package lapes.cesupa.ps_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lapes.cesupa.ps_backend.dto.CreateUser;
import lapes.cesupa.ps_backend.dto.DeliveryCalculateResponse;
import lapes.cesupa.ps_backend.dto.ListDeliveryManResponse;
import lapes.cesupa.ps_backend.model.Order;
import lapes.cesupa.ps_backend.model.Role;
import lapes.cesupa.ps_backend.model.User;
import lapes.cesupa.ps_backend.repository.AddressRepository;
import lapes.cesupa.ps_backend.repository.OrderRepository;
import lapes.cesupa.ps_backend.repository.RoleRepository;
import lapes.cesupa.ps_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final AuthService authService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final AddressRepository addressRepository;

    private final GeocodingService geocodingService;

    private final AddressService addressService;

    public Boolean isValidArea(String address){
        GeocodingService.Coordinate clientCoord = geocodingService.geocode(address);

        var takeawayAddress = addressRepository.findById(1L)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));

        double restaurantLat = takeawayAddress.getLatitude();
        double restaurantLon = takeawayAddress.getLongitude();

        double distance = addressService.calculateDistance(restaurantLat, restaurantLon, clientCoord.latitude(), clientCoord.longitude());
        System.out.println("distance: "+distance);
        // troquei pra 16 porque o gps e bastante impreciso :D
        return distance <= 16;
    }

    public DeliveryCalculateResponse calculate(String address){
        GeocodingService.Coordinate clientCoord = geocodingService.geocode(address);

        var takeawayAddress = addressRepository.findById(1L)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "address not found"));

        double restaurantLat = takeawayAddress.getLatitude();
        double restaurantLon = takeawayAddress.getLongitude();

        double distance = addressService.calculateDistance(restaurantLat, restaurantLon, clientCoord.latitude(), clientCoord.longitude());
        System.out.println("distance: "+distance);
        var taxes = addressService.calculateTaxes(distance);
        return new DeliveryCalculateResponse(distance, taxes);
    }

    public List<ListDeliveryManResponse> listAvailableDrivers(){

        Role deliveryRole = roleRepository.findById(3L)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found"));

        List<User> allUsers = userRepository.findAll();

        Set<UUID> unavailableDrivers = orderRepository.findAll().stream()
                .map(Order::getDeliveryMan)
                .filter(Objects::nonNull)
                .map(User::getId)
                .collect(Collectors.toSet());

        var availableDrivers = allUsers.stream()
                .filter(user -> user.getRoles().contains(deliveryRole))
                .filter(user -> !unavailableDrivers.contains(user.getId()))
                .toList();
        var response = availableDrivers.stream().map(driver -> {
            return new ListDeliveryManResponse(driver.getId(), driver.getUsername(), driver.getPhone());
        }).toList();

        return response;
    }

    public User createDriver(CreateUser dto){
        var deliveryRole = roleRepository.findById(3L)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found"));

        authService.postUserValidation(dto.username(),dto.email());

        var now = LocalDateTime.now();
        var driver = new User();
        driver.setUsername(dto.username());
        driver.setEmail(dto.email());
        driver.setPhone(dto.phone());
        driver.setPassword(bCryptPasswordEncoder.encode(dto.password()));
        driver.setRoles(Set.of(deliveryRole));
        driver.setCreated_at(now);
        driver.setUpdated_at(now);

        return userRepository.save(driver);
    }
}
