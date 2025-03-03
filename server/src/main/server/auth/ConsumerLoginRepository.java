package com.knowMoreQR.server.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConsumerLoginRepository extends JpaRepository<ConsumerLogin, Long> {
    Optional<ConsumerLogin> findByEmail(String email);
}
