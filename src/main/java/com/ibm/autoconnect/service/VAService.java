package com.ibm.autoconnect.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.ibm.autoconnect.utils.MSILConstants;

@Service
public class VAService {

    private final Bucket bucket;
    
    public VAService(Bucket bucket) {
    	this.bucket = bucket;
    }

    public String getCarProbeById(String documentId) {
        try {
            Collection collection = bucket.scope(MSILConstants.couchbaseScope).collection(MSILConstants.PROBECOLLECTIONCOUCH); // adjust if scope/collection differ
            GetResult result = collection.get(documentId);
            return result.contentAsObject().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching CarProbe doc ID: " + documentId, e);
        }
    }
    
    public void mergeAndUpdateDocument(String docId, Map<String, Object> incomingData) {
        try {
            Collection collection = bucket.scope(MSILConstants.couchbaseScope).collection(MSILConstants.PROBECOLLECTIONCOUCH);

            // Step 1: Fetch existing doc
            GetResult result = collection.get(docId);
            JsonObject existingDoc = result.contentAsObject();

            // Step 2: Get or create properties object
            JsonObject props = existingDoc.getObject("properties");
            if (props == null) {
                props = JsonObject.create();
            }

             // Step 2: Merge incoming data into existing doc
            for (Map.Entry<String, Object> entry : incomingData.entrySet()) {
            	String key = entry.getKey();
                Object value = entry.getValue();
            	if (key.equalsIgnoreCase("latitude") || key.equalsIgnoreCase("longitude") 
                        || key.equalsIgnoreCase("timestamp")) {
                        // → update root level
                        existingDoc.put(key, value);
                    } else {
                        // → update/add inside properties
                        props.put(key, value);
                    }
            	props.put(entry.getKey(), entry.getValue()); // Overwrites if exists, adds if new
            }

            // Step 3: Replace in Couchbase
            MutationResult mutation = collection.replace(docId, existingDoc);
            System.out.println("Updated document: " + mutation);

        } catch (Exception e) {
            throw new RuntimeException("Error updating doc: " + docId, e);
        }
    }
    
public void setRemoteProps(String docId,String transcationid) {
	Collection collection = bucket.scope(MSILConstants.couchbaseScope).collection(MSILConstants.VASTATE);
HashMap<String, Object> probeMap = new HashMap<>();

//Step 1: Fetch existing doc
GetResult result = collection.get(docId + "#" + transcationid);
JsonObject existingDoc = result.contentAsObject();

//--- Extract operationType ---
String operationType = existingDoc.getString("operationType");

//--- Extract REMOTEOPS request/response times ---
MSILConstants.REMOTEOP ops[] = MSILConstants.REMOTEOP.values();
for (MSILConstants.REMOTEOP op : ops) {
 String requestKey = op.name() + "RequestTime";
 String responseKey = op.name() + "ResponseTime";

 if (existingDoc.containsKey(requestKey) && existingDoc.get(requestKey) != null) {
     probeMap.put(requestKey, existingDoc.get(requestKey).toString());
 }
 if (existingDoc.containsKey(responseKey) && existingDoc.get(responseKey) != null) {
     probeMap.put(responseKey, existingDoc.get(responseKey).toString());
 }
}

//--- Extract responseTime JSON ---
if (existingDoc.containsKey("responseTime")) {
 JsonObject responseTimeObj = existingDoc.getObject("responseTime");
 for (String key : responseTimeObj.getNames()) {
     probeMap.put(key, responseTimeObj.get(key).toString());
 }
}


String[] returnCodes = {
 "ACReturnCd", "DefrosterReturnCd", "DefoggerReturnCd",
 "FrontDriverSeatVentilationReturnCd", "FrontPassengerSeatVentilationReturnCd",
 "RearDriverSeatVentilationReturnCd", "RearPassengerSeatVentilationReturnCd",
 "RemoteReturnCd", "Temprature"
};

for (String field : returnCodes) {
 if (existingDoc.containsKey(field) && existingDoc.get(field) != null) {
     probeMap.put(field, existingDoc.get(field).toString());
 }
}
mergeAndUpdateDocument(docId+"#"+MSILConstants.REMOTEOPS,probeMap);
//Now probeMap has both root + nested responseTime values
System.out.println("Final probeMap => " + probeMap);
}
			
	

}
