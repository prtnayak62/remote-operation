package com.ibm.autoconnect.entity;

import java.sql.Timestamp;
import java.util.Properties;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import lombok.Data;


@Data
@Document
@Scope(value = "core")
@Collection(value = "vehicle")
public class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationStrategy.UNIQUE)
	private String vinId;
	private String contractId;
	private long nVehicleId;
	private String vehicleId;
	private int vehicleType;
	private int vehicleUsage;
	private int vehicleWidth;
	private int vehicleHeight;
	private int vehicleStatus;
	private String vehicleModel;
	private String vehicleSerialNumber;
	private long nDriverId;
	private String driverId;
	private long nTenantId;
	private String tenantId;
	private int regionId;
	private long mapId;
	private String mapVendorName;
	private String mapVersion;
	private Properties props;
	private Timestamp lastModified;
	private String status;
	private String fwVersion;
	private String hwVersion;
	private String assetType;
	private String assetNum;
	private String siteId;
	public static final int ANONYMOUS_VEHICLE = -1;
}
