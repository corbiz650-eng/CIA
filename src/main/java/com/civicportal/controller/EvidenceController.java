package com.civicportal.controller;

import com.civicportal.dto.evidence.EvidenceResponse;
import com.civicportal.dto.evidence.EvidenceUploadResponse;
import com.civicportal.service.EvidenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EvidenceController {
    private final EvidenceService evidenceService;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    @PostMapping(value = "/complaints/{id}/evidence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvidenceUploadResponse> uploadEvidence(@PathVariable("id") Long complaintId, @RequestParam("file") MultipartFile file) {
        EvidenceUploadResponse response = evidenceService.uploadEvidence(complaintId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/complaints/{id}/evidence")
    public ResponseEntity<List<EvidenceResponse>> getEvidenceForComplaint(@PathVariable("id") Long complaintId) {
        return ResponseEntity.ok(evidenceService.getEvidenceForComplaint(complaintId));
    }

    @GetMapping("/evidence/{id}")
    public ResponseEntity<EvidenceResponse> getEvidenceById(@PathVariable("id") Long evidenceId) {
        return ResponseEntity.ok(evidenceService.getEvidenceById(evidenceId));
    }

    @DeleteMapping("/evidence/{id}")
    public ResponseEntity<Void> deleteEvidence(@PathVariable("id") Long evidenceId) {
        evidenceService.deleteEvidence(evidenceId);
        return ResponseEntity.noContent().build();
    }
}