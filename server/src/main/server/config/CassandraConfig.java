package com.knowMoreQR.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableCassandraRepositories(basePackages = "com.knowMoreQR.server")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Override
    protected String getKeyspaceName() {
        return "knowmoreqr";
    }

    @Override
    protected String getContactPoints() {
        // Change this to your Cassandra host or DataStax Astra connection string
        return "127.0.0.1";
    }

    @Override
    protected int getPort() {
        // Standard Cassandra port, change if using a different port
        return 9042;
    }

    @Override
    public SchemaAction getSchemaAction() {
        // This will create tables if they don't exist
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        CreateKeyspaceSpecification specification = CreateKeyspaceSpecification
                .createKeyspace(getKeyspaceName())
                .ifNotExists()
                .withSimpleReplication(1);
        return Collections.singletonList(specification);
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[] {"com.knowMoreQR.server"};
    }
} 