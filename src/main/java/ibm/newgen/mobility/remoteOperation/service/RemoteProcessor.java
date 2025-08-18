package ibm.newgen.mobility.remoteOperation.service;

import org.json.JSONObject;

public interface RemoteProcessor {
    
    void processMessage(JSONObject payload);
}
