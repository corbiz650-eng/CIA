package com.civicportal.mapper;

import com.civicportal.dto.evidence.EvidenceResponse;
import com.civicportal.dto.evidence.EvidenceUploadResponse;
import com.civicportal.entity.ComplaintEvidence;
import org.springframework.stereotype.Component;

@Component
public class EvidenceMapper {
    public EvidenceResponse toResponse(ComplaintEvidence evidence) {
        if (evidence == null) {
            return null;
        }

        Long complaintId = evidence.getComplaint() == null
                ? null
                : evidence.getComplaint().getId();

        return new EvidenceResponse(
                evidence.getId(),
                complaintId,
                evidence.getFileName(),
                evidence.getFileType(),
                evidence.getFileSize(),
                evidence.getUploadedAt()
        );
    }

    public EvidenceUploadResponse toUploadResponse(
            ComplaintEvidence evidence,
            String message
    ) {
        if (evidence == null) {
            return null;
        }

        Long complaintId = evidence.getComplaint() == null
                ? null
                : evidence.getComplaint().getId();

        return new EvidenceUploadResponse(
                evidence.getId(),
                complaintId,
                evidence.getFileName(),
                evidence.getFileType(),
                evidence.getFileSize(),
                evidence.getUploadedAt(),
                message
        );
    }
}