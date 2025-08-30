package com.ibm.autoconnect.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaProducerConfig {
    
	private final KafkaProperties kafkaProperties;

	public KafkaProducerConfig(KafkaProperties kafkaProperties) {
		this.kafkaProperties = kafkaProperties;
	}

	public Map<String, Object> kakfaProperties() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		configProps.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProperties.getApplicationId());
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configProps.put(SaslConfigs.SASL_MECHANISM, kafkaProperties.getSaslMechanism());
		configProps.put(SaslConfigs.SASL_JAAS_CONFIG, kafkaProperties.getSaslJaasConfig());
		configProps.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS,
				kafkaProperties.getSaslClientCallbackHandlerClass());
		configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getSecurityProtocol());
		return configProps;
	}

	@Bean
	public KafkaProducer<String, String> kafkaProducer() {
		return new KafkaProducer<>(kakfaProperties());
	}
}
