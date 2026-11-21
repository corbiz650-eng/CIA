package com.civicportal.mapper;

import com.civicportal.dto.complaint.ComplaintResponse;
import com.civicportal.dto.complaint.ComplaintSummaryResponse;
import com.civicportal.entity.Complaint;
import org.springframework.stereotype.Component;

@Component
public class ComplaintMapper {
    public ComplaintResponse toResponse(Complaint complaint) {
        if (complaint == null) {
            return null;
        }

        ComplaintResponse response = new ComplaintResponse();
        response.setId(complaint.getId());
        response.setComplaintNumber(complaint.getComplaintNumber());
        response.setDescription(complaint.getDescription());
        response.setStatus(complaint.getStatus());
        response.setPriority(complaint.getPriority());
        response.setAddress(complaint.getAddress());
        response.setLatitude(complaint.getLatitude());
        response.setLongitude(complaint.getLongitude());
        response.setSubmittedAt(complaint.getSubmittedAt());

        if (complaint.getCategory() != null) {
            response.setCategoryId(complaint.getCategory().getId());
            response.setCategoryName(complaint.getCategory().getName());
        }

        if (complaint.getWard() != null) {
            response.setWardId(complaint.getWard().getId());
            response.setWardName(complaint.getWard().getName());
        }

        if (complaint.getCitizen() != null) {
            response.setCitizenId(complaint.getCitizen().getId());
            response.setCitizenName(complaint.getCitizen().getFullName());
        }

        return response;
    }

    public ComplaintSummaryResponse toSummaryResponse(Complaint complaint) {
        if (complaint == null) {
            return null;
        }

        ComplaintSummaryResponse response = new ComplaintSummaryResponse();
        response.setId(complaint.getId());
        response.setComplaintNumber(complaint.getComplaintNumber());
        response.setStatus(complaint.getStatus());
        response.setPriority(complaint.getPriority());
        response.setSubmittedAt(complaint.getSubmittedAt());

        if (complaint.getCategory() != null) {
            response.setCategoryName(complaint.getCategory().getName());
        }

        if (complaint.getWard() != null) {
            response.setWardName(complaint.getWard().getName());
        }

        return response;
    }
}
