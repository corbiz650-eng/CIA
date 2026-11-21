package com.civicportal.controller;

import com.civicportal.dto.assignment.AssignmentRequest;
import com.civicportal.dto.assignment.AssignmentResponse;
import com.civicportal.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class AssignmentController {
    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/complaints/{id}/assign")
    public ResponseEntity<AssignmentResponse> assignComplaint(@PathVariable("id") Long complaintId, @Valid @RequestBody AssignmentRequest request) {
        AssignmentResponse response = assignmentService.assignComplaint(complaintId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/complaints/{id}/reassign")
    public ResponseEntity<AssignmentResponse> reassignComplaint(@PathVariable("id") Long complaintId, @Valid @RequestBody AssignmentRequest request) {
        AssignmentResponse response = assignmentService.assignComplaint(complaintId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/complaints/{id}/assignment")
    public ResponseEntity<List<AssignmentResponse>> getComplaintAssignments(@PathVariable("id") Long complaintId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsForComplaint(complaintId));
    }

    @GetMapping("/officers/{id}/assignment")
    public ResponseEntity<List<AssignmentResponse>> getOfficerAssignments(@PathVariable("id") Long officerId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsForOfficer(officerId));
    }
}