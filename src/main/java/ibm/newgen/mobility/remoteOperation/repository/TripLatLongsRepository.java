package ibm.newgen.mobility.remoteOperation.repository;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import ibm.newgen.mobility.remoteOperation.entity.TripLatLongs;



@Repository
public interface TripLatLongsRepository extends CouchbaseRepository<TripLatLongs, String> {
    
}
