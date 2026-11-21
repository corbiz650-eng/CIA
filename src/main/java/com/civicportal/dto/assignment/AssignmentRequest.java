package com.civicportal.dto.assignment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AssignmentRequest {
    @NotNull(message = "Officer ID is required")
    @Positive(message = "Officer ID must be a positive number")
    private Long officerId;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @Size(max = 1000, message = "Remarks must be 1000 characters or fewer")
    private String remarks;

    public AssignmentRequest() {
    }

    public Long getOfficerId() {
        return officerId;
    }

    public void setOfficerId(Long officerId) {
        this.officerId = officerId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}