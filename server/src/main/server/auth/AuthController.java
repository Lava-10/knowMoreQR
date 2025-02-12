package com.knowMoreQR.server.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.knowMoreQR.server.config.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/consumer/login")
    public ResponseEntity<?> consumerLogin(@RequestBody LoginRequest request) {
        Optional<ConsumerLogin> consumerOpt = consumerRepo.findByEmail(request.getEmail());
        if (consumerOpt.isPresent()) {
            ConsumerLogin consumer = consumerOpt.get();
            if (passwordEncoder.matches(request.getPassword(), consumer.getPasswordHash())) {
                String token = jwtUtil.generateToken(consumer.getEmail(), "consumer", consumer.getId());

                Map<String, Object> response = new HashMap<>();
                response.put("id", consumer.getId());
                response.put("email", consumer.getEmail());
                response.put("userType", "consumer");
                response.put("token", token);
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
                String token = jwtUtil.generateToken(company.getEmail(), "company", company.getId());

                Map<String, Object> response = new HashMap<>();
                response.put("id", company.getId());
                response.put("email", company.getEmail());
                response.put("userType", "company");
                response.put("token", token);
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
    
    @PostMapping("/consumer/register")
    public ResponseEntity<?> registerConsumer(@RequestBody LoginRequest request) {
        if (consumerRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        
        ConsumerLogin consumer = new ConsumerLogin();
        consumer.setEmail(request.getEmail());
        consumer.setName(request.getName() != null ? request.getName() : "Consumer User");
        consumer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        ConsumerLogin savedConsumer = consumerRepo.save(consumer);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Consumer registered successfully");
    }
    
    @PostMapping("/company/register")
    public ResponseEntity<?> registerCompany(@RequestBody LoginRequest request) {
        if (companyRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        
        CompanyLogin company = new CompanyLogin();
        company.setEmail(request.getEmail());
        company.setName(request.getName() != null ? request.getName() : "Company User");
        company.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        CompanyLogin savedCompany = companyRepo.save(company);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Company registered successfully");
    }
}
