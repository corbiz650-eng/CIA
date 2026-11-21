package com.civicportal.controller;

import com.civicportal.dto.complaint.*;
import com.civicportal.entity.ComplaintStatusHistory;
import com.civicportal.service.ComplaintService;
import com.civicportal.service.StatusHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ComplaintController {
    private final ComplaintService complaintService;
    private final StatusHistoryService statusHistoryService;

    public ComplaintController(
            ComplaintService complaintService,
            StatusHistoryService statusHistoryService
    ) {
        this.complaintService = complaintService;
        this.statusHistoryService = statusHistoryService;
    }

    @PostMapping("/complaints")
    public ResponseEntity<ComplaintResponse> createComplaint(@Valid @RequestBody ComplaintCreateRequest request) {
        ComplaintResponse response = complaintService.createComplaint(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/complaints/{id}")
    public ResponseEntity<ComplaintResponse> getComplaint(@PathVariable("id") Long complaintId) {
        return ResponseEntity.ok(complaintService.getComplaintById(complaintId));
    }

    @GetMapping("/complaints/my")
    public ResponseEntity<List<ComplaintSummaryResponse>> getMyComplaints() {
        return ResponseEntity.ok(complaintService.filterComplaints(new ComplaintFilterRequest()));
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<ComplaintSummaryResponse>> filterComplaints(@Valid @ModelAttribute ComplaintFilterRequest request) {
        return ResponseEntity.ok(complaintService.filterComplaints(request));
    }

    @PutMapping("/complaints/{id}")
    public ResponseEntity<ComplaintResponse> updateComplaint(@PathVariable("id") Long complaintId, @Valid @RequestBody ComplaintUpdateRequest request) {
        return ResponseEntity.ok(complaintService.updateComplaint(complaintId, request));
    }

    @PatchMapping("/complaints/{id}/status")
    public ResponseEntity<ComplaintResponse> updateComplaintStatus(@PathVariable("id") Long complaintId, @Valid @RequestBody ComplaintStatusUpdateRequest request) {
        return ResponseEntity.ok(complaintService.updateComplaintStatus(complaintId, request));
    }

    @GetMapping("/complaints/{id}/timeline")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getComplaintTimeline(
            @PathVariable("id") Long complaintId
    ) {
        List<Map<String, Object>> timeline = statusHistoryService
                .getHistoryForComplaint(complaintId)
                .stream()
                .map(this::toTimelineItem)
                .toList();

        return ResponseEntity.ok(timeline);
    }

    private Map<String, Object> toTimelineItem(ComplaintStatusHistory history) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", history.getId());
        item.put("complaintId", history.getComplaint().getId());
        item.put("oldStatus", history.getOldStatus());
        item.put("newStatus", history.getNewStatus());
        item.put("updatedById", history.getChangedBy().getId());
        item.put("updatedByName", history.getChangedBy().getFullName());
        item.put("remarks", history.getRemarks());
        item.put("changedAt", history.getChangedAt());
        return item;
    }
}
