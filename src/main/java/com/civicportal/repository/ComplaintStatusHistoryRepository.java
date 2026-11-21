package com.civicportal.repository;

import com.civicportal.entity.ComplaintStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComplaintStatusHistoryRepository extends JpaRepository<ComplaintStatusHistory, Long> {
    List<ComplaintStatusHistory> findByComplaintIdOrderByChangedAtDesc(Long complaintId);
}