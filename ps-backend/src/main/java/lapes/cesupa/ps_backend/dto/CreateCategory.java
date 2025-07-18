package lapes.cesupa.ps_backend.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategory {
    
    @NotBlank
    private String name;

    private String description;

    private MultipartFile image;

}
