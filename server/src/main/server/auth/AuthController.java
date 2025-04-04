package com.knowMoreQR.server.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ConsumerLoginRepository consumerRepo;

    @Autowired
    private CompanyLoginRepository companyRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/consumer/login")
    public ResponseEntity<?> consumerLogin(@RequestBody LoginRequest request) {
        Optional<ConsumerLogin> consumerOpt = consumerRepo.findByEmail(request.getEmail());
        if (consumerOpt.isPresent()) {
            ConsumerLogin consumer = consumerOpt.get();
            if (passwordEncoder.matches(request.getPassword(), consumer.getPasswordHash())) {
                // Create a simple response with user ID and a mock token
                // In production, use a proper JWT token implementation
                Map<String, Object> response = new HashMap<>();
                response.put("id", consumer.getId());
                response.put("email", consumer.getEmail());
                response.put("token", UUID.randomUUID().toString());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PostMapping("/company/login")
    public ResponseEntity<?> companyLogin(@RequestBody LoginRequest request) {
        Optional<CompanyLogin> companyOpt = companyRepo.findByEmail(request.getEmail());
        if (companyOpt.isPresent()) {
            CompanyLogin company = companyOpt.get();
            if (passwordEncoder.matches(request.getPassword(), company.getPasswordHash())) {
                // Create a simple response with user ID and a mock token
                // In production, use a proper JWT token implementation
                Map<String, Object> response = new HashMap<>();
                response.put("id", company.getId());
                response.put("email", company.getEmail());
                response.put("token", UUID.randomUUID().toString());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
    
    @PostMapping("/consumer/register")
    public ResponseEntity<?> registerConsumer(@RequestBody LoginRequest request) {
        // Check if email already exists
        if (consumerRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        
        // Create and save new consumer
        ConsumerLogin consumer = new ConsumerLogin();
        consumer.setEmail(request.getEmail());
        consumer.setName(request.getName());
        consumer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        ConsumerLogin savedConsumer = consumerRepo.save(consumer);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Consumer registered successfully");
    }
    
    @PostMapping("/company/register")
    public ResponseEntity<?> registerCompany(@RequestBody LoginRequest request) {
        // Check if email already exists
        if (companyRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        
        // Create and save new company
        CompanyLogin company = new CompanyLogin();
        company.setEmail(request.getEmail());
        company.setName(request.getName());
        company.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        CompanyLogin savedCompany = companyRepo.save(company);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Company registered successfully");
    }
}
