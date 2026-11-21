package com.civicportal.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_category")
public class ComplaintCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamps;

    public ComplaintCategory() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(LocalDateTime timestamps) {
        this.timestamps = timestamps;
    }
}
