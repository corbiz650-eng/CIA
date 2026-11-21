package com.civicportal.dto.evidence;

import java.time.LocalDateTime;

public class EvidenceResponse {
    private Long id;
    private Long complaintId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;

    public EvidenceResponse() {
    }

    public EvidenceResponse(Long id, Long complaintId, String fileName, String fileType, Long fileSize, LocalDateTime uploadedAt) {
        this.id = id;
        this.complaintId = complaintId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
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
}