package lapes.cesupa.ps_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lapes.cesupa.ps_backend.dto.CreateCategory;
import lapes.cesupa.ps_backend.dto.ListCategoriesResponse;
import lapes.cesupa.ps_backend.service.CategoryService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/menu")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> create(@ModelAttribute CreateCategory dto) {
        categoryService.create(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ListCategoriesResponse>> listCategories() {
        var categories  = categoryService.listAll();
        return ResponseEntity.ok(categories);
    }
    
    
}
