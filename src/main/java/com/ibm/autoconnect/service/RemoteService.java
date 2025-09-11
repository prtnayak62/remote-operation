package com.ibm.autoconnect.service;

import static com.ibm.autoconnect.rule.constants.TripRuleConstants.VIN_ID;

import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.autoconnect.config.KafkaProperties;
import com.ibm.autoconnect.model.Metadata;
import com.ibm.autoconnect.model.RemoteResponseModel;
import com.ibm.autoconnect.rule.action.Action;
import com.ibm.autoconnect.rule.model.CarProbePayload;
import com.ibm.autoconnect.rule.model.VehiclePayload;
import com.ibm.autoconnect.rule.service.RulesEngineProcessor;
import com.ibm.autoconnect.utils.KafkaNotificationUtils;
import com.ibm.autoconnect.utils.MSILConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RemoteService {
	
	private final KafkaProperties kafkaProperties;
	private final KafkaProducer<String, String> producer;
    private final ApplicationContext context;
    private final Metadata metadata;
    private final RulesEngineProcessor rulesEngineProcessor;
    private final RemoteRulesLoader remoteRulesLoader;
    private final VAService vaService;
    private final RemoteServiceHelper helper;
    
	

	public RemoteService(KafkaProperties kafkaProperties, KafkaProducer<String, String> producer, VAService vaService,
			Metadata metadata, ApplicationContext context, RulesEngineProcessor rulesEngineProcessor,
			RemoteRulesLoader remoteRulesLoader, RemoteServiceHelper helper) {
        this.kafkaProperties = kafkaProperties;
        this.producer = producer;
    	this.metadata = metadata;
        this.context = context;
        this.rulesEngineProcessor=rulesEngineProcessor;
        this.remoteRulesLoader= remoteRulesLoader;
        this.vaService=vaService;
        this.helper = helper;
    }

    public RemoteResponseModel processRemote(List<String> data) {
        try {
        	log.info("Data Size: {}", data.size());
        	for (String payload : data) {
        		JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();
        		
        		String messageId = jsonObject.get("message_id").getAsString();
        		String vin = jsonObject.get("Vin").getAsString();
        		String occurrenceTime = jsonObject.get("RemoteControlResult").getAsJsonObject().get("OccurrenceTime").getAsString();
        		String trackId = String.join("-", vin, messageId, occurrenceTime);
        		metadata.setTrackId(trackId);
        		
        		MDC.put("track_id", metadata.getTrackId());
        		
        		log.info("Payload of remote service {}", jsonObject);
        		
        		RemoteProcessor remoteProcessor = null;
				CarProbePayload carProbePayload=null;
				if(messageId.equals("FT501C")) {
	                 remoteProcessor = context.getBean(messageId, RemoteProcessor.class);
	                }else if(messageId.equals("SSPICN1402")) {
	                	remoteProcessor = context.getBean(messageId, RemoteProcessor.class);
	                }
	                carProbePayload=remoteProcessor.processMessage(jsonObject);
	                VehiclePayload vehiclePayload = helper.getVehiclePayload(jsonObject.get("Vin").getAsString());
	                log.info( " remoteRulesLoader.getRemoteRules() "+remoteRulesLoader.getRemoteRules());
	                List<Action> actions = rulesEngineProcessor.processRemoteRules(carProbePayload, vehiclePayload, remoteRulesLoader.getRemoteRules());
	                this.processAction(actions,carProbePayload);
        	}
        	log.info("Successfully processed the data payload.");   
        } catch (Exception exception) {
            log.error("Exception occurred: ", exception);
        }

        return RemoteResponseModel.builder()
                .result("DONE")
                .statusCode(200)
                .message("Operation successful")
                .build();
    }
        
        
	public void processAction(List<Action> actions, CarProbePayload carProbePayload) {

		for (Action act : actions) {
			if (String.valueOf(act.getActionId()).equalsIgnoreCase(MSILConstants.REMOTECALLRESET_ACTION)) {

				vaService.setRemoteProps(carProbePayload.getProps().getProperty(VIN_ID),
						carProbePayload.getProps().getProperty("xtransactionid"));
				KafkaNotificationUtils.sendRemoteOperationNotification(carProbePayload, act,
						kafkaProperties.getAlertTopic(), producer, metadata.getTrackId());
			}
		}
	}
    
}