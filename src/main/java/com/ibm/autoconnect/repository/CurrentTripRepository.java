package com.ibm.autoconnect.repository;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import com.ibm.autoconnect.entity.CurrentTrip;



@Repository
public interface CurrentTripRepository extends CouchbaseRepository<CurrentTrip, String> {
    
}
