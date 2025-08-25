package com.ibm.autoconnect.repository;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import com.ibm.autoconnect.entity.TripLatLongs;



@Repository
public interface TripLatLongsRepository extends CouchbaseRepository<TripLatLongs, String> {
    
}
