package lapes.cesupa.ps_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lapes.cesupa.ps_backend.dto.CreateUser;
import lapes.cesupa.ps_backend.dto.DeliveryCalculateResponse;
import lapes.cesupa.ps_backend.dto.ListDeliveryManResponse;
import lapes.cesupa.ps_backend.dto.UpdateDeliveryRequest;
import lapes.cesupa.ps_backend.service.DeliveryService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryAreaController {

    private final DeliveryService deliveryService;

    @GetMapping("/areas")
    @PreAuthorize("hasAuthority('SCOPE_COSTUMER')")
    public ResponseEntity<Boolean> validateArea(@RequestParam String address) {
        var isValidArea = deliveryService.isValidArea(address);
        return ResponseEntity.ok(isValidArea);
    }

    @PostMapping("/calculate")
    @PreAuthorize("hasAuthority('SCOPE_COSTUMER')")
    public ResponseEntity<DeliveryCalculateResponse> calculate(@RequestParam String address){
        var response = deliveryService.calculate(address);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/drivers")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<ListDeliveryManResponse>> listDrivers() {
        var deliveryMen = deliveryService.listAvailableDrivers();
        return ResponseEntity.ok(deliveryMen);
    }

    @PostMapping("/drivers")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> createDriver(@RequestBody CreateUser dto) {
        deliveryService.createDriver(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/drivers/{id}/status")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> updateDriverStatus(@PathVariable Long id, @RequestBody UpdateDeliveryRequest dto) {
        deliveryService.updateDriverStatus(id, dto);
        return ResponseEntity.ok().build();
    }
    
    
}
