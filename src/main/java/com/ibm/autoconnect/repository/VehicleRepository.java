package com.ibm.autoconnect.repository;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import com.ibm.autoconnect.entity.Vehicle;

@Repository
public interface VehicleRepository extends CouchbaseRepository<Vehicle, String>  {

}
