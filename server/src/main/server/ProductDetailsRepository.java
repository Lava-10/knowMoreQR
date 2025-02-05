package com.knowMoreQR.server;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProductDetailsRepository extends CassandraRepository<ProductDetails, UUID> {
    // Additional query methods can be added here if needed
} 