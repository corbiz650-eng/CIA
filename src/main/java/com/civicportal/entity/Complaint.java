package com.civicportal.entity;

import com.civicportal.enums.ComplaintPriority;
import com.civicportal.enums.ComplaintStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complaint_number", nullable = false, unique = true, length = 20)
    private String complaintNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "citizen_id", nullable = false)
    private User citizen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private ComplaintCategory category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(nullable = false, length = 500)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplaintPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ComplaintStatus status;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    public Complaint() {
    }

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getComplaintNumber() {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber){
        this.complaintNumber = complaintNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }

    public User getCitizen() {
        return citizen;
    }

    public void setCitizen(User citizen) {
        this.citizen = citizen;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}
