package com.civicportal.mapper;

import com.civicportal.dto.investigation.InvestigationResponse;
import com.civicportal.entity.Investigation;
import org.springframework.stereotype.Component;

@Component
public class InvestigationMapper {
    public InvestigationResponse toResponse(Investigation investigation) {
        if (investigation == null) {
            return null;
        }

        InvestigationResponse response = new InvestigationResponse();
        response.setId(investigation.getId());
        response.setStartedAt(investigation.getStartedAt());
        response.setCompletedAt(investigation.getCompletedAt());
        response.setFindings(investigation.getFindings());
        response.setOffenderIdentified(investigation.getOffenderIdentified());
        response.setRemarks(investigation.getRemarks());

        if (investigation.getComplaint() != null) {
            response.setComplaintId(investigation.getComplaint().getId());
            response.setComplaintNumber(investigation.getComplaint().getComplaintNumber());
        }

        if (investigation.getInvestigator() != null) {
            response.setInvestigatorId(investigation.getInvestigator().getId());
            response.setInvestigatorName(investigation.getInvestigator().getFullName());
        }

        return response;
    }
}