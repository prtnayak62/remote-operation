package com.ibm.autoconnect.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.autoconnect.entity.Vehicle;
import com.ibm.autoconnect.repository.VehicleRepository;
import com.ibm.autoconnect.rule.model.VehiclePayload;

@Component
public class RemoteServiceHelper {

	private VehiclePayload vehiclePayload = null;
	private Vehicle vehicle = null;
	
	private final VehicleRepository repository;
	private final ObjectMapper mapper;
	
	public RemoteServiceHelper(VehicleRepository repository, ObjectMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}
	
	public VehiclePayload getVehiclePayload(String vin) {
		if (Objects.isNull(vehiclePayload)) {
			vehicle = getVehicle(vin);
			if (Objects.nonNull(vehicle)) {
				vehiclePayload = mapper.convertValue(vehicle, VehiclePayload.class);
			}
		}
		return vehiclePayload;
	}
	
	public Vehicle getVehicle(String vin) {
		if (Objects.isNull(vehicle)) {
			Optional<Vehicle> vehicleOptional = repository.findById(vin);
			vehicle = vehicleOptional.isPresent() ? vehicleOptional.get() : null;
		}
		return vehicle;
	}
}
