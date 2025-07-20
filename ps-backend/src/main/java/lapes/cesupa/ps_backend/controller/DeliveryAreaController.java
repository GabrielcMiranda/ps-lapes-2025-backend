package lapes.cesupa.ps_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lapes.cesupa.ps_backend.service.DeliveryService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryAreaController {

    private final DeliveryService deliveryService;

    @GetMapping("/areas")
    public ResponseEntity<Boolean> validateArea(@RequestParam String address) {
        var isValidArea = deliveryService.isValidArea(address);
        return ResponseEntity.ok(isValidArea);
    }
    
}
