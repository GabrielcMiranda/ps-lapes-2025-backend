package lapes.cesupa.ps_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    private final String uploadCategoriesDir;

    public CategoryService(CategoryRepository categoryRepository, ImageService imageService,@Value("${app.upload.categories-path}") String uploadCategoriesDir) {
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
        this.uploadCategoriesDir = uploadCategoriesDir;
    }

    @Transactional
    public Category create(CreateCategory dto) {
        validateUniqueName(dto.getName());

        String imageUrl = null;

        if (dto.getImage() != null && !dto.getImage().isEmpty()){
            imageService.validateImageType(dto.getImage().getContentType());
            imageUrl = imageService.storeImage(uploadCategoriesDir,dto.getImage());
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

        return categories.stream()
            .map(category -> {
                var imageUrl = imageService.generateCategoryImageUrl(category);
                
                return new ListCategoriesResponse(
                    category.getName(),
                    category.getDescription(),
                    imageUrl
                );
            })
            .toList();
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
            var imageUrl = imageService.storeImage(uploadCategoriesDir, dto.getImage());
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

    public List<Category> findAllById(Set<Long> ids){
        var categories = categoryRepository.findAllById(ids);
        if(categories.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"no categories were found");
        }

        return categories;
    }

    
    
}
