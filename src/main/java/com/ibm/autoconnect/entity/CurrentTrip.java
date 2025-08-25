package com.ibm.autoconnect.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Scope(value = "trip_scope")
@Collection(value = "current_trip_collection")
@Document
public class CurrentTrip {
    
    @Id
	@GeneratedValue(strategy = GenerationStrategy.UNIQUE)
	private String vin;

    private String currentTripId;

    private String currentTripStartTime;

    private String lastPacketReceived;

    private boolean isProcessed;
}
