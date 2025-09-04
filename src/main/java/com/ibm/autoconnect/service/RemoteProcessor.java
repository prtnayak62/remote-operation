package com.ibm.autoconnect.service;

import com.google.gson.JsonObject;
import com.ibm.autoconnect.rule.model.CarProbePayload;

public interface RemoteProcessor {
    
    CarProbePayload processMessage(JsonObject payload);
}
