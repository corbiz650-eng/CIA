package com.civicportal.repository;

import com.civicportal.entity.Investigation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestigationRepository extends JpaRepository<Investigation, Long> {
    List<Investigation> findByComplaintId(Long complaintId);
    List<Investigation> findByInvestigatorId(Long investigatorId);
}