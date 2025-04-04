package com.knowMoreQR.server;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TagRepository extends CassandraRepository<Tag, UUID> {
    // You can add custom query methods here if needed, for example:
    // List<Tag> findByCompanyId(String companyId);
    // List<Tag> findByName(String name);
} 