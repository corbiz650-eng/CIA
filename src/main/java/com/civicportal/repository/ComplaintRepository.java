package com.civicportal.repository;

import com.civicportal.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>,
        JpaSpecificationExecutor<Complaint> {
    Optional<Complaint> findByComplaintNumber(String complaintNumber);
    boolean existsByComplaintNumber(String complaintNumber);
}
