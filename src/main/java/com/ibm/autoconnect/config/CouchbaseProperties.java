package com.ibm.autoconnect.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CouchbaseProperties {

    private final String connectionString;
    private final String username;
    private final String password;
    private final String bucket;

    public CouchbaseProperties(
            @Value("${couchbase-properties.credentials}") String credentialsJson,
            @Value("${couchbase-properties.bucket}") String bucket
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(credentialsJson);

            this.connectionString = node.get("connectionString").asText();
            this.username = node.get("username").asText();
            this.password = node.get("password").asText();
            this.bucket = bucket;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Couchbase credentials JSON", e);
        }
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBucket() {
        return bucket;
    }
}
