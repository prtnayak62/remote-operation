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

	private String mskClusterArn;
	private String applicationId;
	private String probeTopic;
	private String alertTopic;
	private String securityProtocol = "SASL_SSL";
	private String saslMechanism = "AWS_MSK_IAM";
	private String saslJaasConfig = "software.amazon.msk.auth.iam.IAMLoginModule required;";
	private String saslClientCallbackHandlerClass = "software.amazon.msk.auth.iam.IAMClientCallbackHandler";
}