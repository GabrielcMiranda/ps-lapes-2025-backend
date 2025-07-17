package lapes.cesupa.ps_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        // ver se realmente mando todos os atributos e conferir se ta certo o id
        var categories = categoryRepository.findAll();

        if(categories.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no categories were found");
        }

        List<ListCategoriesResponse> response = new ArrayList<>();

        categories.stream().forEach(category -> response.add(new ListCategoriesResponse(category.getName(),category.getDescription(),category.getImageUrl())));

        return response;
    }

    private void validateUniqueName(String name){
        if(categoryRepository.findByName(name).isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "category name already exists");
        }
    }

    
    
}
