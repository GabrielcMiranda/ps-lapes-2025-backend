package lapes.cesupa.ps_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lapes.cesupa.ps_backend.repository.AddressRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

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
}
