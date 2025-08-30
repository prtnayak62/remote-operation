package com.ibm.autoconnect.config;

import java.util.Objects;

import org.json.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "couchbase-properties")
@Setter
@Getter
@Component
public class CouchbaseProperties {

	private String credentials;
	private String bucket;

	protected String getUsername() {
		if (Objects.nonNull(this.getCredentials())) {
			JSONObject obj = new JSONObject(this.getCredentials());
			if (!obj.isEmpty()) {
				return obj.getString("username");
			}
		}
		return null;
	}

	protected String getPassword() {
		if (Objects.nonNull(this.getCredentials())) {
			JSONObject obj = new JSONObject(this.getCredentials());
			if (!obj.isEmpty()) {
				return obj.getString("password");
			}
		}
		return null;
	}

	protected String getConnectionString() {
		if (Objects.nonNull(this.getCredentials())) {
			JSONObject obj = new JSONObject(this.getCredentials());
			if (!obj.isEmpty()) {
				return obj.getString("connectionString");
			}
		}
		return null;
	}
}
