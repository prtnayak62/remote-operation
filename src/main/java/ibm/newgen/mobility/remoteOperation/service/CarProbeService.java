package ibm.newgen.mobility.remoteOperation.service;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.kv.GetResult;
import org.springframework.stereotype.Service;
import ibm.newgen.mobility.remoteOperation.config.CouchbaseProperties;

@Service
public class CarProbeService {

    private final Cluster cluster;
    private final Bucket bucket;

    public CarProbeService(CouchbaseProperties couchbaseProperties) {
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
}
