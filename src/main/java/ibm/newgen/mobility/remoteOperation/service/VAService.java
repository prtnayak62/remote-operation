package ibm.newgen.mobility.remoteOperation.service;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ibm.newgen.mobility.remoteOperation.config.CouchbaseProperties;
import ibm.newgen.mobility.remoteOperation.utils.MSILConstants;

@Service
public class VAService {

    private final Cluster cluster;
    private final Bucket bucket;

    public VAService(CouchbaseProperties couchbaseProperties) {
        // Connect using env variable values
        this.cluster = Cluster.connect(
                couchbaseProperties.getConnectionString(),
                couchbaseProperties.getUsername(),
                couchbaseProperties.getPassword()
        );

        this.bucket = cluster.bucket(couchbaseProperties.getBucket());
        this.bucket.waitUntilReady(java.time.Duration.ofSeconds(10));
    }

    public String getCarProbeById(String documentId) {
        try {
            Collection collection = bucket.scope("dev").collection("car-probe"); // adjust if scope/collection differ
            GetResult result = collection.get(documentId);
            return result.contentAsObject().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching CarProbe doc ID: " + documentId, e);
        }
    }
    
    public void mergeAndUpdateDocument(String docId, Map<String, Object> incomingData) {
        try {
            Collection collection = bucket.scope("dev").collection("car-probe");

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
    
private void setRemoteProps(String docId,String transcationid) {
	 Collection collection = bucket.scope("dev").collection("va-state");
	 HashMap<String,Object> probeMap = new HashMap<>();
     // Step 1: Fetch existing doc
     GetResult result = collection.get(docId+'#'+transcationid);
	//
		
			JsonObject existingDoc = result.contentAsObject();
			 String operationType = existingDoc.getString("operationType");
			 MSILConstants.REMOTEOPS ops[] = MSILConstants.REMOTEOPS.values();
				
			
							//System.out.println("Contents of the enum are: " + MSILConstants.REMOTEOPS.values());
						// Iterating enum using the for loop
						for (MSILConstants.REMOTEOPS op : ops) {

							 String requestKey = op.name() + "RequestTime";
						        if (existingDoc.containsKey(requestKey) && existingDoc.get(requestKey) != null) {
						        	probeMap.put(requestKey, existingDoc.get(requestKey).toString());
						        }

						        // responseTime key
						        String responseKey = op.name() + "ResponseTime";
						        if (existingDoc.containsKey(responseKey) && existingDoc.get(responseKey) != null) {
						        	probeMap.put(responseKey, existingDoc.get(responseKey).toString());
						        }
						}
						 if (existingDoc.get("ACReturnCd") != null) {
							 probeMap.put("ACReturnCd", existingDoc.get("ACReturnCd").toString());
						 }
						 if (existingDoc.get("DefrosterReturnCd") != null) {
							 probeMap.put("DefrosterReturnCd", existingDoc.get("DefrosterReturnCd").toString());
						 }

						 if (existingDoc.get("DefoggerReturnCd") != null) {
							 probeMap.put("DefoggerReturnCd", existingDoc.get("DefoggerReturnCd").toString());
						 }
						 if (existingDoc.get("FrontDriverSeatVentilationReturnCd") != null) {
							 probeMap.put("FrontDriverSeatVentilationReturnCd", existingDoc.get("FrontDriverSeatVentilationReturnCd").toString());
						 }
						 if (existingDoc.get("FrontPassengerSeatVentilationReturnCd") != null) {
							 probeMap.put("FrontPassengerSeatVentilationReturnCd", existingDoc.get("FrontPassengerSeatVentilationReturnCd").toString());
						 }

						 if (existingDoc.get("RearDriverSeatVentilationReturnCd") != null) {
							 probeMap.put("RearDriverSeatVentilationReturnCd", existingDoc.get("RearDriverSeatVentilationReturnCd").toString());
						 }
						 if (existingDoc.get("RearPassengerSeatVentilationReturnCd") != null) {
							 probeMap.put("RearPassengerSeatVentilationReturnCd", existingDoc.get("RearPassengerSeatVentilationReturnCd").toString());
						 }

						 if (existingDoc.get("RemoteReturnCd") != null) {
							 probeMap.put("RemoteReturnCd", existingDoc.get("RemoteReturnCd").toString());
						 }
						 if (existingDoc.get("Temprature") != null) {
							 probeMap.put("Temprature", existingDoc.get("Temprature").toString());
						 }

						
}
			
	

}
