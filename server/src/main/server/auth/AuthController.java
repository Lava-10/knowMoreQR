package com.knowMoreQR.server.auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
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
                // Optionally generate and return a JWT token here
                return ResponseEntity.ok("Consumer login successful");
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
                // Optionally generate and return a JWT token here
                return ResponseEntity.ok("Company login successful");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
