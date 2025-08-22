package ibm.newgen.mobility.remoteOperation.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.autoconnect.rule.action.Action;
import com.ibm.autoconnect.rule.model.CarProbePayload;
import com.ibm.autoconnect.rule.model.VehiclePayload;
import com.ibm.autoconnect.rule.service.RulesEngineProcessor;


import ibm.newgen.mobility.remoteOperation.model.RemoteResponseModel;
import ibm.newgen.mobility.remoteOperation.utils.RemoteUtils;

import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class RemoteService {

    private final ApplicationContext context;
    private final ObjectMapper objectMapper;
    private final RulesEngineProcessor rulesEngineProcessor;
    private final RemoteRulesLoader tripRulesLoader;

    public RemoteService(ObjectMapper objectMapper, ApplicationContext context,RulesEngineProcessor rulesEngineProcessor,RemoteRulesLoader tripRulesLoader) {
        this.objectMapper = objectMapper;
        this.context = context;
        this.rulesEngineProcessor=rulesEngineProcessor;
        this.tripRulesLoader= tripRulesLoader;
    }

    public RemoteResponseModel processRemote(Object data) {
        try {
        	String payload;
		
				payload = (String) RemoteUtils.convertObjectToString(data, objectMapper);
				JSONObject jsonObject = new JSONObject(payload);
				RemoteProcessor remoteProcessor = null;
				CarProbePayload carProbePayload=null;
                String messageId = jsonObject.optString("message_id");
                if(messageId.equals("FT501C")) {
                 remoteProcessor = context.getBean(messageId, RemoteProcessor.class);
                }else if(messageId.equals("SSPICN1402")) {
                	remoteProcessor = context.getBean(messageId, RemoteProcessor.class);
                }
                carProbePayload=remoteProcessor.processMessage(jsonObject);
                Properties vehicleProps = new Properties();
                vehicleProps.setProperty("VEH_GEN", "2.5");
                vehicleProps.setProperty("VEHICLE_IDENTITY", "842741.0");
                vehicleProps.setProperty("ALERT_TRIPON", "1.0");
                VehiclePayload vehiclePayload = VehiclePayload.builder().props(vehicleProps).build();
                List<Action> actions = rulesEngineProcessor.processTripRules(carProbePayload, vehiclePayload, tripRulesLoader.getTripRules());
                System.out.print("action"+actions);
			
           
           
        } catch (Exception exception) {
            log.error("Exception occurred: ", exception);
        }

        return RemoteResponseModel.builder()
                .result("DONE")
                .statusCode(200)
                .message("Operation successful")
                .build();
    }
}
