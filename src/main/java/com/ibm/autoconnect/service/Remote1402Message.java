package com.ibm.autoconnect.service;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.autoconnect.model.CarProbe;
import com.ibm.autoconnect.model.RemoteControlCallback;
import com.ibm.autoconnect.model.RemoteControlGen3Result;
import com.ibm.autoconnect.repository.VehicleInputPayload;
import com.ibm.autoconnect.rule.model.CarProbePayload;
import com.ibm.autoconnect.utils.RemoteUtils;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "SSPICN1402")
public class Remote1402Message implements RemoteProcessor {

    private final ObjectMapper objectMapper;

    public Remote1402Message(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public CarProbePayload processMessage(JSONObject data) {
    	HashMap<String,Object> probeMap = new HashMap<>();
    	Properties props = new Properties();
    	CarProbePayload carProbePayload=new CarProbePayload();
       
    	
        try {
        	RemoteControlGen3Result remoteControlResult = objectMapper.readValue(data.toString(), RemoteControlGen3Result.class);

        	this.populateCommonHeader(remoteControlResult, probeMap);
        	 CarProbe cacheCarProbe = CarProbe.builder().build();//agent car probe cache table integration from couchbase
        	 log.info("cacheCarProbe: " + cacheCarProbe);
        	 probeMap.put("AppRequestNo", remoteControlResult.getCorrelation_id());
 			if(null!=remoteControlResult.getIsInServiceMode()) {
 				probeMap.put("IsInServiceMode", remoteControlResult.getIsInServiceMode());
 			}
 			probeMap.put("RequestStatus", remoteControlResult.getRequestStatus());
 			probeMap.put("RequestType", remoteControlResult.getRequestType());
 				if(remoteControlResult.getRequestStatus() != null && remoteControlResult.getRequestStatus().equalsIgnoreCase("ResponseSuccess")) {
 				probeMap.put("Status", "0");
 			}else if(remoteControlResult.getRequestStatus() != null && remoteControlResult.getRequestStatus().equalsIgnoreCase("ResponseFailure")){
 				probeMap.put("Status", "1");
 			}else if(remoteControlResult.getRequestStatus() != null && remoteControlResult.getRequestStatus().equalsIgnoreCase("ResponseTimeout")){
 				probeMap.put("Status", "2");
 			}
 			
 			probeMap.put("RemoteStartResult", remoteControlResult.getResultCode());
 			probeMap.put("CenterTimeStamp", RemoteUtils.getFormattedEventDate(remoteControlResult.getCenterTimestamp()));
 			probeMap.put("RequestReceptionTime", RemoteUtils.getFormattedEventDate(remoteControlResult.getRequestReceptionTime()));
 			probeMap.put("OperationEndTime", RemoteUtils.getFormattedEventDate(remoteControlResult.getOperationEndTime()));
 			probeMap.put("DataCreationTime", RemoteUtils.getFormattedEventDate(remoteControlResult.getDataCreationTime()));
 			probeMap.put("OccurrenceTime", RemoteUtils.getFormattedEventDate(remoteControlResult.getDataCreationTime()));
 			probeMap.put("sensing.stimestamp",RemoteUtils.getFormattedEventDate(remoteControlResult.getDataCreationTime()));
 			if(remoteControlResult.getPositionInfo() != null) {
 			probeMap.put("MajorAxisError", remoteControlResult.getPositionInfo().getMajorAxisError());
 			probeMap.put("MinorAxisError", remoteControlResult.getPositionInfo().getMinorAxisError());
 			probeMap.put("sensing.longitude", remoteControlResult.getPositionInfo().getLongitude());
 			probeMap.put("sensing.latitude", remoteControlResult.getPositionInfo().getLatitude());	
 			probeMap.put("LHeading", remoteControlResult.getPositionInfo().getHeading());
 			probeMap.put("vehicle.ddirection", remoteControlResult.getPositionInfo().getHeading());
 			probeMap.put("sensing.altitude", remoteControlResult.getPositionInfo().getAltitude());
 			probeMap.put("PositionValidFlag", remoteControlResult.getPositionInfo().isPositionValidFlag());
 			probeMap.put("MajorAxisGradient", remoteControlResult.getPositionInfo().getMajorAxisGradient());
 			probeMap.put("GeoidHeight", remoteControlResult.getPositionInfo().getGeoidHeight());
 			probeMap.put("GeodeticReferenceSystem", remoteControlResult.getPositionInfo().getGeodeticReferenceSystem());
 			
 			if(remoteControlResult.getPositionInfo().getAcquisitionDateTime() != null && !remoteControlResult.getPositionInfo().getAcquisitionDateTime().isEmpty())
 			probeMap.put("AcquisitionDatetime", RemoteUtils.getFormattedPOSDate(remoteControlResult.getPositionInfo().getAcquisitionDateTime()));
 			}
 			probeMap.put("RemoteClimateStatus", remoteControlResult.getRemoteClimateStatus());
 			probeMap.put("RemoteBatteryPreconStatus", remoteControlResult.getBatteryPreconStatus());
 			
 			

 			probeMap.put("ts", RemoteUtils.getFormattedEventDate(remoteControlResult.getTscgwtime()));

 			probeMap.put("xtransactionid", remoteControlResult.getCorrelation_id());

 		
 			if ((probeMap.get("sensing.stimestamp") == null || probeMap.get("sensing.stimestamp").equals(""))) {
 				if(remoteControlResult.getTscgwtime() != null ) {
 					probeMap.put("sensing.stimestamp", RemoteUtils.getFormattedPOSDate(remoteControlResult.getTscgwtime()));
 					
 				}
 			}
 			

 			

 			probeMap.put("ignition_status","0");	

 			boolean isInvalid = validateGPSPoints(probeMap);

// 			if(isInvalid) {
// 				if(cacheCarProbObject!=null) {
// 					this.populatePositionInfo(cacheCarProbObject, probeMap);
// 					this.processCarProbe(probeMap);
// 				}
// 			}else {
// 				this.processCarProbe(probeMap);
// 			}
 			//System.out.println("remoteControlResult.getCorrelation_id() "+remoteControlResult.getCorrelation_id());
 			
 			System.out.println("probeMap "+probeMap);
 			for (Map.Entry<String, Object> entry : probeMap.entrySet()) {
			    props.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
			}	
			   carProbePayload = CarProbePayload.builder().props(props).build();

              // TODO: Fetch CarProbe data from DB
            
            
        } catch (JsonProcessingException exception) {
//            log.error("Exception occurred while processing message: ", exception);
        }
		return carProbePayload;
    }

   
    
    protected  HashMap<String,Object> populateCommonHeader(VehicleInputPayload vehicleInputPayload, HashMap<String,Object> probeMap) {
    	
		probeMap.put("message_id", vehicleInputPayload.getMessageId());
		probeMap.put("action", "SEND_CARPROBE");

		probeMap.put("IMEI", vehicleInputPayload.getVin());

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
