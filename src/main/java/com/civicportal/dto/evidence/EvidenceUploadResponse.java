package com.civicportal.dto.evidence;

import java.time.LocalDateTime;

public class EvidenceUploadResponse {
    private Long evidenceId;
    private Long complaintId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private String message;

    public EvidenceUploadResponse() {
    }

    public EvidenceUploadResponse(Long evidenceId, Long complaintId, String fileName, String fileType, Long fileSize, LocalDateTime uploadedAt, String message) {
        this.evidenceId = evidenceId;
        this.complaintId = complaintId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.message = message;
    }

    public Long getEvidenceId() {
        return evidenceId;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public String getMessage() {
        return message;
    }
}