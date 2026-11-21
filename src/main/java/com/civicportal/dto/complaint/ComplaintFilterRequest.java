package com.civicportal.dto.complaint;

import com.civicportal.enums.ComplaintPriority;
import com.civicportal.enums.ComplaintStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ComplaintFilterRequest {
    private ComplaintStatus status;

    private ComplaintPriority priority;

    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;

    @Positive(message = "Ward ID must be a positive number")
    private Long wardId;

    @Size(max = 30, message = "Complaint number must be 30 characters or fewer")
    private String complaintNumber;

    private LocalDate createdFrom;

    private LocalDate createdTo;

    public ComplaintFilterRequest() {
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public ComplaintPriority getPriority() {
        return priority;
    }

    public void setPriority(ComplaintPriority priority) {
        this.priority = priority;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getWardId() {
        return wardId;
    }

    public void setWardId(Long wardId) {
        this.wardId = wardId;
    }

    public String getComplaintNumber() {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber) {
        this.complaintNumber = complaintNumber;
    }

    public LocalDate getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(LocalDate createdFrom) {
        this.createdFrom = createdFrom;
    }

    public LocalDate getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo(LocalDate createdTo) {
        this.createdTo = createdTo;
    }
}