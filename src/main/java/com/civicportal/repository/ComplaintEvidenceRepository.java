package com.civicportal.repository;

import com.civicportal.entity.ComplaintEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComplaintEvidenceRepository extends JpaRepository<ComplaintEvidence, Long> {
    List<ComplaintEvidence> findByComplaintId(Long complaintId);
}