package com.civicportal.mapper;

import com.civicportal.dto.ward.WardResponse;
import com.civicportal.entity.Ward;
import org.springframework.stereotype.Component;

@Component
public class WardMapper {
    public WardResponse toResponse(Ward ward) {
        if (ward == null) {
            return null;
        }

        return new WardResponse(
                ward.getId(),
                ward.getCode(),
                ward.getName(),
                ward.getDescription(),
                ward.isActive()
        );
    }
}