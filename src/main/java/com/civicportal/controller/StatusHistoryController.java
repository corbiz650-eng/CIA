package com.civicportal.controller;

import com.civicportal.entity.ComplaintStatusHistory;
import com.civicportal.service.StatusHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class StatusHistoryController {
    private final StatusHistoryService statusHistoryService;

    public StatusHistoryController(StatusHistoryService statusHistoryService) {
        this.statusHistoryService = statusHistoryService;
    }

    @GetMapping("/complaints/{id}/status-history")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getComplaintStatusHistory(@PathVariable("id") Long complaintId) {
        List<Map<String, Object>> response = statusHistoryService
                .getHistoryForComplaint(complaintId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> toResponse(ComplaintStatusHistory history) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", history.getId());
        response.put("complaintId", history.getComplaint().getId());
        response.put("oldStatus", history.getOldStatus());
        response.put("newStatus", history.getNewStatus());
        response.put(" changedById", history.getChangedBy().getId());
        response.put(" changedByName", history.getChangedBy().getFullName());
        response.put("remarks", history.getRemarks());
        response.put("changedAt", history.getChangedAt());
        return response;
    }
}