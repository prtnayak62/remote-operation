package com.ibm.autoconnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "kafka-properties")
@Setter
@Getter
@Component
public class KafkaProperties {

	private String bootstrapServers;
	private String applicationId;
	private String securityProtocol;
	private String alertTopic;
	private String saslMechanism;
	private String saslJaasConfig;
	private String saslClientCallbackHandlerClass;
}