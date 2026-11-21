package com.civicportal.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "investigations")
public class Investigation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "investigator_id", nullable = false)
    private User investigator;

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String findings;

    @Column(name = "offender_identified", nullable = false)
    private String offenderIdentified;

    @Column(nullable = false)
    private String remarks;

    public Investigation() {
    }

    @PrePersist
    protected void onCreate() {
        this.startedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public User getInvestigator() {
        return investigator;
    }

    public void setInvestigator(User investigator) {
        this.investigator = investigator;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
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