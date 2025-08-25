package com.ibm.autoconnect.config;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.Bucket;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouchbaseConfiguration {

    @Value("${couchbase-properties.credentials}")
    private String credentialsJson;

    @Value("${couchbase-properties.bucket}")
    private String bucketName;

    @Bean
    public ClusterEnvironment couchbaseEnvironment() {
        return ClusterEnvironment.builder().build();
    }

    @Bean
    public Cluster couchbaseCluster(ClusterEnvironment environment) throws Exception {
        // Parse JSON string from environment variable
        ObjectMapper mapper = new ObjectMapper();
        JsonNode creds = mapper.readTree(credentialsJson);

        String username = creds.get("username").asText();
        String password = creds.get("password").asText();
        String connectionString = creds.get("connectionString").asText();

        return Cluster.connect(connectionString, username, password);
    }

    @Bean
    public Bucket couchbaseBucket(Cluster cluster) {
        return cluster.bucket(bucketName);
    }
}
