package com.ibm.autoconnect.service;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.ibm.autoconnect.model.CarProbe;
import com.ibm.autoconnect.model.RemoteControlCallback;
import com.ibm.autoconnect.repository.VehicleInputPayload;
import com.ibm.autoconnect.rule.model.CarProbePayload;
import com.ibm.autoconnect.utils.MSILConstants;
import com.ibm.autoconnect.utils.RemoteUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "FT501C")
public class Remote501CMessage implements RemoteProcessor {
	private final CarProbeService carProbeService;
	  private final ObjectMapper objectMapper;

    @Autowired
    public Remote501CMessage(CarProbeService carProbeService,ObjectMapper objectMapper) {
        this.carProbeService = carProbeService;
		this.objectMapper = new ObjectMapper();
    }
    

    @Override
    public CarProbePayload processMessage(JsonObject data) {
    	HashMap<String,Object> probeMap = new HashMap<>();
    	Properties props = new Properties();
    	CarProbePayload carProbePayload=new CarProbePayload();
        
    	
        try {
        	RemoteControlCallback remoteControlResult = objectMapper.readValue(data.toString(),
        			RemoteControlCallback.class);
          
        	this.populateCommonHeader(remoteControlResult, probeMap);
        	 CarProbe cacheCarProbe = CarProbe.builder().build();//agent car probe cache table integration from couchbase
        	 log.info("cacheCarProbe: " + probeMap);
			probeMap.put("AppRequestNo", remoteControlResult.getAppRequestNo());
			if(remoteControlResult.getRequestResult() != null) {
			probeMap.put("Status", remoteControlResult.getRequestResult().getStatus());
			probeMap.put("Phase", remoteControlResult.getRequestResult().getPhase());
			}
			String carProbeData = carProbeService.getCarProbeById(remoteControlResult.getVin()+"#"+MSILConstants.REMOTEOPS);
	        System.out.println("CarProbe Data: " + carProbeData);
	       if(remoteControlResult.getRemoteControlResult()!=null) {

				probeMap.put("OccurrenceTime", RemoteUtils.getFormattedEventDate(remoteControlResult.getRemoteControlResult().getOccurrenceTime()));
				probeMap.put("DcmDormantDatetime", RemoteUtils.getFormattedEventDate(remoteControlResult.getRemoteControlResult().getDcmDormantDatetime()));
				probeMap.put("AcquisitionDatetime", RemoteUtils.getFormattedEventDate(remoteControlResult.getRemoteControlResult().getPositionInfo().getAcquisitionDatetime()));

				probeMap.put("MajorAxisError", remoteControlResult.getRemoteControlResult().getPositionInfo().getDcmPositionAccuracy().getMajorAxisError());
				probeMap.put("MinorAxisError", remoteControlResult.getRemoteControlResult().getPositionInfo().getDcmPositionAccuracy().getMinorAxisError());

				probeMap.put("CenterTimeStamp", RemoteUtils.getFormattedEventDate(remoteControlResult.getRemoteControlResult().getTimeInformation().getCenterTimeStamp()));
				probeMap.put("RequestReceptionTime", RemoteUtils.getFormattedEventDate(remoteControlResult.getRemoteControlResult().getTimeInformation().getRequestReceptionTime()));
				probeMap.put("OperationEndTime", RemoteUtils.getFormattedEventDate(remoteControlResult.getRemoteControlResult().getTimeInformation().getOperationEndTime()));
				probeMap.put("StandbyStartTime", RemoteUtils.getFormattedEventDate(remoteControlResult.getRemoteControlResult().getTimeInformation().getStandbyStartTime()));

				
				probeMap.put("RemoteStartResult", remoteControlResult.getRemoteControlResult().getRemoteStartResult());
				probeMap.put("AirConditioningSettingsResult", remoteControlResult.getRemoteControlResult().getAirConditioningSettingsResult());
				probeMap.put("VentilationResult", remoteControlResult.getRemoteControlResult().getVentilationResult());
				probeMap.put("RemoteHvacResultParameter1", remoteControlResult.getRemoteControlResult().getRemoteHvacResultParameter1());
				probeMap.put("RemoteHvacResultParameter2", remoteControlResult.getRemoteControlResult().getRemoteHvacResultParameter2());
				
				probeMap.put("longitude", remoteControlResult.getRemoteControlResult().getPositionInfo().getLongitude());
				probeMap.put("latitude", remoteControlResult.getRemoteControlResult().getPositionInfo().getLatitude());			
				probeMap.put("ts", RemoteUtils.getFormattedEventDate(remoteControlResult.getTscgwtime()));

				probeMap.put("xtransactionid", remoteControlResult.getXtransactionid());
				probeMap.put("timestamp",RemoteUtils.getFormattedPOSDate(remoteControlResult.getRemoteControlResult().getOccurrenceTime()));
				log.info("probeMap: " + probeMap);
				
				carProbeService.mergeAndUpdateDocument(remoteControlResult.getVin()+"#"+MSILConstants.REMOTEOPS, probeMap);
				for (Map.Entry<String, Object> entry : probeMap.entrySet()) {
				    props.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
				}	
				   carProbePayload = CarProbePayload.builder().props(props).build();

			}
	       log.info("Successfully processed the data payload.");
              // TODO: Fetch CarProbe data from DB
            
            
        } catch (JsonProcessingException exception) {
//            log.error("Exception occurred while processing message: ", exception);
        }
		return carProbePayload;
    }

   
    
    protected  HashMap<String,Object> populateCommonHeader(VehicleInputPayload vehicleInputPayload, HashMap<String,Object> probeMap) {
    	
		probeMap.put("message_id", vehicleInputPayload.getMessageId());
		probeMap.put("action", "SEND_CARPROBE");

		probeMap.put("vinId", vehicleInputPayload.getVin());
		probeMap.put("vehicle_id", "DEFREG:"+vehicleInputPayload.getVin());
		probeMap.put("res", "sync");

		//Vehicle vehicle = config.getVm().getfromExternalID("DEFREG:"+vehicleInputPayload.getVin());

//		if(vehicle!=null && vehicle.getProps() !=null && vehicle.getProps().getProperty("ANONYMOUS_ID")!=null) {
//
//			probeMap.put("ANONYMOUS_ID", vehicle.getProps().getProperty("ANONYMOUS_ID"));
//		}			
		return probeMap;
	}
    
    private int gpsValidStatus(boolean isInvalid) {
		int gpsValid;
		if(isInvalid) {
			//0 is invalid
			gpsValid = 0;
		}else {
			//1 is valid
			gpsValid = 1;
		}
		return gpsValid;
	}
    
    protected  boolean validateGPSPoints(Map<String,Object> probeMap) {

		boolean isInvalid = false;

		if(probeMap.get("sensing.longitude")==null || probeMap.get("sensing.longitude").equals("null") ||				
				probeMap.get("sensing.longitude").toString().equals("-9999.0") 
				|| probeMap.get("sensing.latitude").toString().equals("-9999.0")) {
			isInvalid=true;
		}else if(probeMap.get("MajorAxisError")!=null && probeMap.get("MinorAxisError")!=null) {

			int majorValue = (int) probeMap.get("MajorAxisError");
			int minorValue = (int) probeMap.get("MinorAxisError");

			if(majorValue>150 || minorValue>150) {
				isInvalid=true;
			}

		}else if((probeMap.get("hdop")!=null && probeMap.get("numsat")!=null)) {

			double hdopValue = Double.parseDouble((String) probeMap.get("hdop"));
			double numStatValue = Double.parseDouble((String) probeMap.get("numsat"));

			if(numStatValue<5 || hdopValue>7) {
				isInvalid=true;
			}
		}
		return isInvalid;
	}
    
    protected  Map<String,Object> populatePositionInfo(CarProbe cacheCarProbObject, Map<String,Object> probeMap) {

		probeMap.put("sensing.longitude", cacheCarProbObject.getLongitude());
		probeMap.put("sensing.latitude", cacheCarProbObject.getLatitude());	
		probeMap.put("hdop", cacheCarProbObject.getProps().getProperty("hdop"));
		probeMap.put("numsat", cacheCarProbObject.getProps().getProperty("numsat"));
		return probeMap;

	}
   
}
