package com.civicportal.service;

import com.civicportal.dto.evidence.EvidenceResponse;
import com.civicportal.dto.evidence.EvidenceUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EvidenceService {

    EvidenceUploadResponse uploadEvidence(Long complaintId, MultipartFile file);

    List<EvidenceResponse> getEvidenceForComplaint(Long complaintId);

    EvidenceResponse getEvidenceById(Long evidenceId);

    void deleteEvidence(Long evidenceId);
}
