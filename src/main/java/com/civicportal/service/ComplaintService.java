package com.civicportal.service;

import com.civicportal.dto.complaint.*;

import java.util.List;

public interface ComplaintService {

    ComplaintResponse createComplaint(ComplaintCreateRequest request);
    ComplaintResponse updateComplaint(Long complaintId, ComplaintUpdateRequest request);
    ComplaintResponse getComplaintById(Long complaintId);
    List<ComplaintSummaryResponse> filterComplaints(ComplaintFilterRequest request);
    ComplaintResponse updateComplaintStatus(
            Long complaintId,
            ComplaintStatusUpdateRequest request
    );

}
