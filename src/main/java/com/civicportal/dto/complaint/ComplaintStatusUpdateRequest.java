package com.civicportal.dto.complaint;

import com.civicportal.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ComplaintStatusUpdateRequest {
    @NotNull(message = "New status is required")
    private ComplaintStatus status;

    @Size(max = 1000, message = "Remarks must be 1000 characters or fewer")
    private String remarks;

    public ComplaintStatusUpdateRequest() {
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}