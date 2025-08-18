package ibm.newgen.mobility.remoteOperation.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import ibm.newgen.mobility.remoteOperation.model.RemoteResponseModel;
import ibm.newgen.mobility.remoteOperation.utils.RemoteUtils;

import java.util.List;

@Slf4j
@Service
public class RemoteService {

    private final ApplicationContext context;
    private final ObjectMapper objectMapper;

    public RemoteService(ObjectMapper objectMapper, ApplicationContext context) {
        this.objectMapper = objectMapper;
        this.context = context;
    }

    public RemoteResponseModel processRemote(Object data) {
        try {
        	String payload;
		
				payload = (String) RemoteUtils.convertObjectToString(data, objectMapper);
				JSONObject jsonObject = new JSONObject(payload);
				RemoteProcessor remoteProcessor = null;
                String messageId = jsonObject.optString("message_id");
                if(messageId.equals("FT501C")) {
                 remoteProcessor = context.getBean(messageId, RemoteProcessor.class);
                }else if(messageId.equals("SSPICN1402")) {
                 remoteProcessor = context.getBean(messageId, RemoteProcessor.class);
                }
                remoteProcessor.processMessage(jsonObject);
			
           
           
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
