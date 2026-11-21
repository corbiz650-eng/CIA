package com.civicportal.repository;

import com.civicportal.entity.ComplaintAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintAssignmentRepository extends JpaRepository<ComplaintAssignment, Long> {
    List<ComplaintAssignment> findByComplaintId(Long complaintId);
    List<ComplaintAssignment> findByComplaintIdAndActiveTrue(Long complaintId);
    List<ComplaintAssignment> findByOfficerId(Long officerId);
    List<ComplaintAssignment> findByOfficerIdAndActiveTrue(Long officerId);
}
