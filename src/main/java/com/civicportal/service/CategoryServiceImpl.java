package com.civicportal.service;

import com.civicportal.dto.category.CategoryRequest;
import com.civicportal.dto.category.CategoryResponse;
import com.civicportal.entity.ComplaintCategory;
import com.civicportal.exception.BadRequestException;
import com.civicportal.exception.DuplicateResourceException;
import com.civicportal.exception.ResourceNotFoundException;
import com.civicportal.mapper.CategoryMapper;
import com.civicportal.repository.ComplaintCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final ComplaintCategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(
            ComplaintCategoryRepository categoryRepository,
            CategoryMapper categoryMapper
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String name = normalizeName(request.getName());
        ensureNameAvailable(name);

        ComplaintCategory category = new ComplaintCategory();
        category.setName(name);
        category.setDescription(normalizeDescription(request.getDescription()));

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(
            Long categoryId,
            CategoryRequest request
    ) {
        ComplaintCategory category = findCategory(categoryId);
        String name = normalizeName(request.getName());

        if (!name.equalsIgnoreCase(category.getName())) {
            ensureNameAvailable(name);
        }

        category.setName(name);
        category.setDescription(normalizeDescription(request.getDescription()));
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        return categoryMapper.toResponse(findCategory(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse setCategoryActive(Long categoryId, boolean active) {
        ComplaintCategory category = findCategory(categoryId);
        category.setActive(active);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    private ComplaintCategory findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Complaint category not found with ID: " + categoryId));
    }

    private void ensureNameAvailable(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                    "A complaint category with this name already exists");
        }
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Category name must not be blank");
        }
        return name.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BadRequestException(
                    "Category description must not be blank");
        }
        return description.trim();
    }
}
