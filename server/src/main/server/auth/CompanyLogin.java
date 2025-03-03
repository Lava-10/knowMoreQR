package com.knowMoreQR.server.auth;

import javax.persistence.*;

@Entity
@Table(name = "company_login")
public class CompanyLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    // Stored as a hashed value (e.g., BCrypt)
    @Column(nullable = false)
    private String passwordHash;

    public CompanyLogin() {}

    public CompanyLogin(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
