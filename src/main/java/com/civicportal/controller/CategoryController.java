package com.civicportal.controller;

import com.civicportal.dto.category.CategoryRequest;
import com.civicportal.dto.category.CategoryResponse;
import com.civicportal.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable("id") Long categoryId, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deactivateCategory(@PathVariable("id") Long categoryId) {
        categoryService.setCategoryActive(categoryId, false);
        return ResponseEntity.noContent().build();
    }
}