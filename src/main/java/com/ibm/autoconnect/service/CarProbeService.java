package com.ibm.autoconnect.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.ibm.autoconnect.utils.MSILConstants;

@Service
public class CarProbeService {
	
    private final Bucket bucket;
    
    public CarProbeService(Bucket bucket) {
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
}
