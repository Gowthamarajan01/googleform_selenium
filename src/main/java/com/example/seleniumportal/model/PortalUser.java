package com.example.seleniumportal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

@Entity
@Table(name = "portal_users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_portal_users_username", columnNames = "username")
})
public class PortalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String fullName;

    @Column(nullable = false, length = 30)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column
    private LocalDate dob;

    @Column(length = 10)
    private String gender;

    @Column(length = 10)
    private String department;

    @Column(length = 1)
    private String section;

    @Column(nullable = false)
    private boolean receiveUpdates;

    protected PortalUser() {
    }

    public PortalUser(
            String fullName,
            String username,
            String password,
            LocalDate dob,
            String gender,
            String department,
            String section,
            boolean receiveUpdates) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.department = department;
        this.section = section;
        this.receiveUpdates = receiveUpdates;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getDepartment() {
        return department;
    }

    public String getSection() {
        return section;
    }

    public boolean isReceiveUpdates() {
        return receiveUpdates;
    }
}
