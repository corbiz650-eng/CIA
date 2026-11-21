package com.civicportal.service;

import com.civicportal.dto.assignment.AssignmentRequest;
import com.civicportal.dto.assignment.AssignmentResponse;

import java.util.List;

public interface AssignmentService {

    AssignmentResponse assignComplaint(Long complaintId, AssignmentRequest request);

    List<AssignmentResponse> getAssignmentsForComplaint(Long complaintId);

    List<AssignmentResponse> getAssignmentsForCurrentOfficer();

    List<AssignmentResponse> getAssignmentsForOfficer(Long officerId);

    AssignmentResponse completeAssignment(Long assignmentId);
}
