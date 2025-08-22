package ibm.newgen.mobility.remoteOperation.service;

import org.json.JSONObject;

import com.ibm.autoconnect.rule.model.CarProbePayload;

public interface RemoteProcessor {
    
    CarProbePayload processMessage(JSONObject payload);
}
