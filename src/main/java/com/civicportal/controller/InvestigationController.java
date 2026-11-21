package com.civicportal.controller;

import com.civicportal.dto.investigation.InvestigationCreateRequest;
import com.civicportal.dto.investigation.InvestigationResponse;
import com.civicportal.dto.investigation.InvestigationUpdateRequest;
import com.civicportal.service.InvestigationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class InvestigationController {
    private final InvestigationService investigationService;

    public InvestigationController(InvestigationService investigationService) {
        this.investigationService = investigationService;
    }

    @PostMapping("/complaints/{id}/investigation")
    public ResponseEntity<InvestigationResponse> createInvestigation(@PathVariable("id") Long complaintId, @Valid @RequestBody InvestigationCreateRequest request) {
        InvestigationResponse response = investigationService.createInvestigation(complaintId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/complaints/{id}/investigation")
    public ResponseEntity<List<InvestigationResponse>> getInvestigations(@PathVariable("id") Long complaintId) {
        return ResponseEntity.ok(investigationService.getInvestigationsForComplaint(complaintId));
    }

    @PutMapping("/investigations/{id}")
    public ResponseEntity<InvestigationResponse> updateInvestigation(@PathVariable("id") Long investigationId, @Valid @RequestBody InvestigationUpdateRequest request) {
        return ResponseEntity.ok(investigationService.updateInvestigation(investigationId, request));
    }

    @PostMapping("/investigations/{id}/complete")
    public ResponseEntity<InvestigationResponse> completeInvestigation(@PathVariable("id") Long investigationId) {
        return ResponseEntity.ok(investigationService.completeInvestigation(investigationId));
    }
}