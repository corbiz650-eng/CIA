package com.civicportal.dto.investigation;

import java.time.LocalDateTime;

public class InvestigationResponse {
    private Long id;
    private Long complaintId;
    private String complaintNumber;
    private Long investigatorId;
    private String investigatorName;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String findings;
    private String offenderIdentified;
    private String remarks;

    public InvestigationResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getComplaintNumber() {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber) {
        this.complaintNumber = complaintNumber;
    }

    public Long getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(Long investigatorId) {
        this.investigatorId = investigatorId;
    }

    public String getInvestigatorName() {
        return investigatorName;
    }

    public void setInvestigatorName(String investigatorName) {
        this.investigatorName = investigatorName;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public String getOffenderIdentified() {
        return offenderIdentified;
    }

    public void setOffenderIdentified(String offenderIdentified) {
        this.offenderIdentified = offenderIdentified;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}