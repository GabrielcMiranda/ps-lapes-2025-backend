package lapes.cesupa.ps_backend.dto;

public record DeliveryAddressRequest(
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String complement
    ) {}
