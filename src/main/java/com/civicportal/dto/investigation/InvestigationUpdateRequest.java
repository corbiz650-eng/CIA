package com.civicportal.dto.investigation;

import jakarta.validation.constraints.Size;

public class InvestigationUpdateRequest {
    @Size(max = 5000, message = "Findings must be 5000 characters or fewer")
    private String findings;

    @Size(max = 5000, message = "Remarks must be 5000 characters or fewer")
    private String remarks;

    public InvestigationUpdateRequest() {
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}