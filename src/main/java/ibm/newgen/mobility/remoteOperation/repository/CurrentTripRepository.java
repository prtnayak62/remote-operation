package ibm.newgen.mobility.remoteOperation.repository;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import ibm.newgen.mobility.remoteOperation.entity.CurrentTrip;



@Repository
public interface CurrentTripRepository extends CouchbaseRepository<CurrentTrip, String> {
    
}
