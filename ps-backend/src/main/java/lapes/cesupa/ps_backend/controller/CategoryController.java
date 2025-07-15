package lapes.cesupa.ps_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lapes.cesupa.ps_backend.dto.CreateCategory;
import lapes.cesupa.ps_backend.model.Category;
import lapes.cesupa.ps_backend.repository.CategoryRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/menu")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    private final String uploadCategoriesDir;

    public CategoryController(CategoryRepository categoryRepository, @Value("${app.upload.categories-path}") String uploadCategoriesDir) {
        this.categoryRepository = categoryRepository;
        this.uploadCategoriesDir = uploadCategoriesDir;
    }


    @Transactional
    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<String> create(@ModelAttribute CreateCategory dto) {
        var categoryFromDb = categoryRepository.findByName(dto.getName());

        if(categoryFromDb.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        var now = LocalDateTime.now();

        var category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setCreatedAt(now);
        category.setUpdatedAt(now);

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            var contentType = dto.getImage().getContentType();
            if (!contentType.equals("image/png") && !contentType.equals("image/jpeg") && !contentType.equals("image/webp")) {
                throw new RuntimeException("invalid format");
            }
            String fileName = UUID.randomUUID() + "-" + dto.getImage().getOriginalFilename();
            File dest = new File(uploadCategoriesDir + fileName);
            try {
                dto.getImage().transferTo(dest);
                category.setImageUrl("/" + uploadCategoriesDir + fileName); // ou só fileName
            } catch (IOException e) {
                throw new RuntimeException("Error while saving the image", e);
            }
        }else{
            category.setImageUrl(null);
        }

        categoryRepository.save(category);

        return ResponseEntity.ok().build();
    }
    
}
