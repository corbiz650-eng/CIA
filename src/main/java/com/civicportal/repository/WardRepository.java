package com.civicportal.repository;

import com.civicportal.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface WardRepository extends JpaRepository<Ward, Long> {
    Optional<Ward> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeIgnoreCase(String code);

    List<Ward> findByActiveTrue();
}
