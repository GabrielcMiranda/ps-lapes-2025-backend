package lapes.cesupa.ps_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lapes.cesupa.ps_backend.dto.CreateCategory;
import lapes.cesupa.ps_backend.dto.CreateItem;
import lapes.cesupa.ps_backend.dto.ListCategoriesResponse;
import lapes.cesupa.ps_backend.dto.ListItemResponse;
import lapes.cesupa.ps_backend.service.CategoryService;
import lapes.cesupa.ps_backend.service.ImageService;
import lapes.cesupa.ps_backend.service.ItemService;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/menu")
public class MenuController {

    private final ImageService imageService;

    private final ItemService itemService;

    private final CategoryService categoryService;

    private final String uploadCategoriesDir;

    public MenuController(CategoryService categoryService, ItemService itemService, ImageService imageService, @Value("${app.upload.categories-path}") String uploadCategoriesDir) {
        this.categoryService = categoryService;
        this.itemService = itemService;
        this.imageService = imageService;
        this.uploadCategoriesDir = uploadCategoriesDir;
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> createCategory(@ModelAttribute CreateCategory dto) {
        categoryService.create(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/categoryImages/{filename}")
    public ResponseEntity<Resource> getCategoryImage(@PathVariable String filename) {
        System.out.println(">> Tentando buscar imagem: " + filename);
        System.out.println(">> Diretório base: " + uploadCategoriesDir);
        return imageService.getImage(uploadCategoriesDir, filename); 
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ListCategoriesResponse>> listCategories() {
        var categories  = categoryService.listAll();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> updateCategory(@PathVariable Long id, @ModelAttribute CreateCategory dto) {
        categoryService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/items")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> createItem(@RequestBody CreateItem dto) {
        itemService.create(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items")
    public ResponseEntity<Page<ListItemResponse>> listItems(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Integer minPrice,
        @RequestParam(required = false) Integer maxPrice,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id,asc") String[] sort
        ){
        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);
        
        return ResponseEntity.ok(itemService.listAll(categoryId, minPrice, maxPrice, search, pageable));
    }

    
    
}
