package com.civicportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wards")
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    protected Ward() {
    }

    public Ward(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}