package com.ibm.autoconnect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.env.ClusterEnvironment;

@Configuration
public class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {

    private final CouchbaseProperties couchbaseProperties;
    
    public CouchbaseConfiguration(CouchbaseProperties couchbaseProperties) {
    	this.couchbaseProperties = couchbaseProperties;
    }

    @Override
    public String getBucketName() {
        return couchbaseProperties.getBucket();
    }

    @Override
    public String getConnectionString() {
        return couchbaseProperties.getConnectionString();
    }

    @Override
    public String getPassword() {
        return couchbaseProperties.getPassword();
    }

    @Override
    public String getUserName() {
        return couchbaseProperties.getUsername();
    }
    
    
    @Override
	public ClusterEnvironment couchbaseClusterEnvironment() {
		return ClusterEnvironment.builder().securityConfig(s -> s.enableTls(couchbaseProperties.isSsl())).build();
	}
    
    @Override
    @Bean
    public CustomConversions customConversions() {
          return super.customConversions();
    }
    
    @Bean
    public Bucket getBucket(Cluster cluster) {
    	return cluster.bucket(getBucketName());
    }
}
