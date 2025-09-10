package com.ibm.autoconnect.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.kafka.KafkaClient;
import software.amazon.awssdk.services.kafka.model.GetBootstrapBrokersRequest;
import software.amazon.awssdk.services.kafka.model.GetBootstrapBrokersResponse;

@Service
public class MskBootstrapService {

   @Value("${MSK_CONSUMER_CLUSTER_ARN}")
   private String mskClusterArn;
   
   private final KafkaClient kafkaClient = KafkaClient.create();

   public String getBootstrapServers() {
       if (mskClusterArn == null || mskClusterArn.trim().isEmpty()) {
           throw new RuntimeException("MSK_CONSUMER_CLUSTER_ARN environment variable not configured");
       }
       
       if (!mskClusterArn.startsWith("arn:aws:kafka:")) {
           throw new RuntimeException("Invalid MSK_CONSUMER_CLUSTER_ARN format: " + mskClusterArn);
       }
       
       try {
           GetBootstrapBrokersRequest request = GetBootstrapBrokersRequest.builder()
               .clusterArn(mskClusterArn)
               .build();
           
           GetBootstrapBrokersResponse response = kafkaClient.getBootstrapBrokers(request);
           
           // Try SASL/IAM first (works for both serverless and provisioned)
           String bootstrapServers = response.bootstrapBrokerStringSaslIam();
           if (bootstrapServers != null && !bootstrapServers.trim().isEmpty()) {
               return bootstrapServers;
           }
           
           // Fallback to plain bootstrap string
           bootstrapServers = response.bootstrapBrokerString();
           if (bootstrapServers != null && !bootstrapServers.trim().isEmpty()) {
               return bootstrapServers;
           }
           
           throw new RuntimeException("No bootstrap servers found in MSK response");
       } catch (Exception e) {
           throw new RuntimeException("Failed to get bootstrap servers: " + e.getMessage(), e);
       }
   }
}