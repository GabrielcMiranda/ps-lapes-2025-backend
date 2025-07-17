package lapes.cesupa.ps_backend.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImageService {

    private final String uploadCategoriesDir;

    public ImageService(@Value("${app.upload.categories-path}") String uploadCategoriesDir){
        this.uploadCategoriesDir = uploadCategoriesDir;
    }
    
    public void validateImageType(String contentType){
        if (!List.of("image/png", "image/jpeg", "image/webp").contains(contentType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "only png, jpg and webp are valid formats");            
            }
    }

    public String storeImage(MultipartFile image){
        String fileName = UUID.randomUUID() + "-" + image.getOriginalFilename();
            File dest = new File(uploadCategoriesDir + fileName);
            try {
                System.out.println("Working dir: " + System.getProperty("user.dir"));
                System.out.println("Original filename: " + image.getOriginalFilename());
                System.out.println("Saving file to: " + dest.getAbsolutePath());
                image.transferTo(dest);
                return "/" + uploadCategoriesDir + fileName;
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while saving the image");
            }
    }
}
