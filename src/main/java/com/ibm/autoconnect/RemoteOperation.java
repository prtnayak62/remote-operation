package com.ibm.autoconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@SpringBootApplication(scanBasePackages = {
     "com.ibm.autoconnect"               // external/shared rule engine package
})
public class RemoteOperation {

    public static void main(String[] args) {
        SpringApplication.run(RemoteOperation.class, args);
    }
}
