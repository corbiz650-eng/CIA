package com.civicportal.service;

import com.civicportal.dto.category.CategoryRequest;
import com.civicportal.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long categoryId, CategoryRequest request);

    CategoryResponse getCategoryById(Long categoryId);

    List<CategoryResponse> getAllCategories();

    CategoryResponse setCategoryActive(Long categoryId, boolean active);
}
