package lapes.cesupa.ps_backend.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lapes.cesupa.ps_backend.model.Category;


@Service
public class ImageService {
    
    public void validateImageType(String contentType){
        if (!List.of("image/png", "image/jpeg", "image/webp").contains(contentType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "only png, jpg and webp are valid formats");            
            }
    }

    public String storeImage(String dirPath, MultipartFile image){
        String fileName = UUID.randomUUID() + "-" + image.getOriginalFilename();
            
            try {
                Path filePath = Paths.get(dirPath, fileName);
                File dest = filePath.toFile();

                System.out.println("Working dir: " + System.getProperty("user.dir"));
                System.out.println("Original filename: " + image.getOriginalFilename());
                System.out.println("Saving file to: " + dest.getAbsolutePath());

                image.transferTo(dest);
                return filePath.toAbsolutePath().toString();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while saving the image");
            }
    }

    public ResponseEntity<Resource> getImage(String filepath,String filename){
        Path imagePath = Paths.get(filepath, filename);
        System.out.println("Imagem buscada em: " + imagePath.toAbsolutePath());
        if (!Files.exists(imagePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "image not found");
        }
        Resource resource;
        try {
            resource = new UrlResource(imagePath.toUri());

            MediaType mediaType = MediaTypeFactory
                .getMediaType(imagePath.getFileName().toString())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
            .contentType(mediaType)
            .body(resource);

        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while loading image");
        }
    }

    public String generateCategoryImageUrl(Category category){

        String imageUrl = null;
        String imagePathStr = category.getImageUrl();
        if (category.getImageUrl() != null) {
            try {

                String baseUrl = ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .build()
                                .toUriString();

                Path fullPath = Paths.get(imagePathStr.replaceFirst("^/(?!/)", ""));
                String fileName = fullPath.getFileName().toString();
                imageUrl = baseUrl + "/menu/categoryImages/" + fileName;
            } catch (Exception e) {
                e.printStackTrace();
                imageUrl = null;
            }
        }
        return imageUrl;
    }

     public String generateItemImageUrl(String image){

        String imageUrl = null;
        if (image != null) {
            try {

                String baseUrl = ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .build()
                                .toUriString();
                                
                Path fullPath = Paths.get(image.replaceFirst("^/(?!/)", ""));
                String fileName = fullPath.getFileName().toString();
                imageUrl = baseUrl + "/menu/itemImages/" + fileName;
            } catch (Exception e) {
                e.printStackTrace();
                imageUrl = null;
            }
        }
        return imageUrl;
    }
}