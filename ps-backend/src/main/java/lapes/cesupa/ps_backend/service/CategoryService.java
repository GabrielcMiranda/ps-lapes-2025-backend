package lapes.cesupa.ps_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lapes.cesupa.ps_backend.dto.CreateCategory;
import lapes.cesupa.ps_backend.dto.ListCategoriesResponse;
import lapes.cesupa.ps_backend.model.Category;
import lapes.cesupa.ps_backend.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final ImageService imageService;

    public CategoryService(CategoryRepository categoryRepository, ImageService imageService) {
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
    }

    @Transactional
    public Category create(CreateCategory dto) {
        validateUniqueName(dto.getName());

        String imageUrl = null;

        if (dto.getImage() != null && !dto.getImage().isEmpty()){
            imageService.validateImageType(dto.getImage().getContentType());
            imageUrl = imageService.storeImage(dto.getImage());
        }

        var now = LocalDateTime.now();

        var category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        category.setImageUrl(imageUrl);

        return categoryRepository.save(category);
    }

    public List<ListCategoriesResponse> listAll(){

        var categories = categoryRepository.findAll();

        if(categories.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no categories were found");
        }

        List<ListCategoriesResponse> response = new ArrayList<>();

        categories.stream().forEach(category -> response.add(new ListCategoriesResponse(category.getName(),category.getDescription(),category.getImageUrl())));

        return response;
    }

    @Transactional
    public Category update(Long id, CreateCategory dto){
        var category = validateCategoryId(id);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            category.setName(dto.getName());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            category.setDescription(dto.getDescription());
        }

        if (dto.getImage() != null && !dto.getImage().isEmpty()){
            imageService.validateImageType(dto.getImage().getContentType());
            var imageUrl = imageService.storeImage(dto.getImage());
            category.setImageUrl(imageUrl);
        }

        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(category);
    }

    @Transactional
    public Category delete(Long id){
        var category = validateCategoryId(id);

        categoryRepository.delete(category);

        return category;
    }

    private void validateUniqueName(String name){
        if(categoryRepository.findByName(name).isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "category name already exists");
        }
    }

    private Category validateCategoryId(Long id){
        var category = categoryRepository.findById(id);
        if(category.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category not found");
        }

        return category.get();
    }

    
    
}
