package com.civicportal.service;

import com.civicportal.dto.investigation.InvestigationCreateRequest;
import com.civicportal.dto.investigation.InvestigationResponse;
import com.civicportal.dto.investigation.InvestigationUpdateRequest;

import java.util.List;

public interface InvestigationService {
    InvestigationResponse createInvestigation(Long complaintId, InvestigationCreateRequest request);
    List<InvestigationResponse> getInvestigationsForComplaint(Long complaintId);
    InvestigationResponse updateInvestigation(Long investigationId, InvestigationUpdateRequest request);
    InvestigationResponse completeInvestigation(Long investigationId);
}