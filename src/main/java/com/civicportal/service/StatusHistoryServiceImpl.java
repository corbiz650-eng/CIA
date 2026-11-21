package com.civicportal.service;

import com.civicportal.entity.ComplaintStatusHistory;
import com.civicportal.exception.ResourceNotFoundException;
import com.civicportal.repository.ComplaintRepository;
import com.civicportal.repository.ComplaintStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StatusHistoryServiceImpl implements StatusHistoryService {
    private final ComplaintRepository complaintRepository;
    private final ComplaintStatusHistoryRepository statusHistoryRepository;

    public StatusHistoryServiceImpl(
            ComplaintRepository complaintRepository,
            ComplaintStatusHistoryRepository statusHistoryRepository
    ) {
        this.complaintRepository = complaintRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintStatusHistory> getHistoryForComplaint(Long complaintId) {
        if (!complaintRepository.existsById(complaintId)) {
            throw new ResourceNotFoundException(
                    "Complaint not found with ID: " + complaintId
            );
        }

        return statusHistoryRepository
                .findByComplaintIdOrderByChangedAtDesc(complaintId);
    }
}