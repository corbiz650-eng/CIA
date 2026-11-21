package com.civicportal.mapper;

import com.civicportal.dto.category.CategoryResponse;
import com.civicportal.entity.ComplaintCategory;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(ComplaintCategory category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive()
        );
    }
}