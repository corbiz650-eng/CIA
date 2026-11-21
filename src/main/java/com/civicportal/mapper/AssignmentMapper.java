package com.civicportal.mapper;

import com.civicportal.dto.assignment.AssignmentResponse;
import com.civicportal.entity.ComplaintAssignment;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {
    public AssignmentResponse toResponse(ComplaintAssignment assignment) {
        if (assignment == null) {
            return null;
        }

        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setAssignedAt(assignment.getAssignedAt());
        response.setDueDate(assignment.getDueDate());
        response.setCompletedAt(assignment.getCompletedAt());
        response.setActive(assignment.isActive());
        response.setRemarks(assignment.getRemarks());

        if (assignment.getComplaint() != null) {
            response.setComplaintId(assignment.getComplaint().getId());
            response.setComplaintNumber(assignment.getComplaint().getComplaintNumber());
        }

        if (assignment.getOfficer() != null) {
            response.setOfficerId(assignment.getOfficer().getId());
            response.setOfficerName(assignment.getOfficer().getFullName());
        }

        if (assignment.getAssignedBy() != null) {
            response.setAssignedById(assignment.getAssignedBy().getId());
            response.setAssignedByName(assignment.getAssignedBy().getFullName());
        }

        return response;
    }
}