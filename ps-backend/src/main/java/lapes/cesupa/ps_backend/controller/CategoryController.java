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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;




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
    public ResponseEntity<List<ListCategoriesResponse>> list() {
        var categories  = categoryService.listAll();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long id, @ModelAttribute CreateCategory dto) {
        categoryService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }

    
    
}
