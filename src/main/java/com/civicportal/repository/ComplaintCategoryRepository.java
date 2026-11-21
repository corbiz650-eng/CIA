package com.civicportal.repository;

import com.civicportal.entity.ComplaintCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComplaintCategoryRepository extends JpaRepository<ComplaintCategory, Long> {
    Optional<ComplaintCategory> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameIgnoreCase(String name);
}
