package lapes.cesupa.ps_backend.dto;

import org.springframework.web.multipart.MultipartFile;

public record AddItemPhoto(MultipartFile[] files) {

}
