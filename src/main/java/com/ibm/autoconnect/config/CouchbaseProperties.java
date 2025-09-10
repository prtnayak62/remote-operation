package com.ibm.autoconnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "couchbase-properties")
@Setter
@Getter
@Component
public class CouchbaseProperties {

	private String connectionString;
	private String username;
	private String password;
	private String bucket;
}
