package com.knowMoreQR.server.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyLoginRepository extends JpaRepository<CompanyLogin, Long> {
    Optional<CompanyLogin> findByEmail(String email);
}
