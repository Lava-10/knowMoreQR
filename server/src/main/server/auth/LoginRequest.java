package com.knowMoreQR.server.auth;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest() {}

    // Getters and setters
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
