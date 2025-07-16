package lapes.cesupa.ps_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUser(

    @NotBlank
    String username, 
    
    @NotBlank
    @Email
    String email, 
    
    @NotBlank
    String password, 
    
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}\\-?\\d{4}$", message = "invalid phone number")
    String phone) {}
