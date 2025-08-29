package com.ibm.autoconnect.utils;

import java.util.HashMap;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.autoconnect.rule.action.Action;
import com.ibm.autoconnect.rule.model.CarProbePayload;

import ch.qos.logback.classic.Logger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaNotificationUtils {
	 private KafkaNotificationUtils() {
    }

	 public void sendRemoteOperationNotification(CarProbePayload cb, Action action) {

			//DevLogger.info("--------------------inside sendAlertNotification--------------");
			JSONObject obj = new JSONObject();


			String[] saved_props = action.getContents().toString().split(",");

			HashMap<String, String> objMap = new HashMap<>();
			//System.out.println("key=" + action.getContents().toString());
			//if(null !=action.getAction_id() && !action.getAction_id().equalsIgnoreCase("REMOTEABNORMAL")) {
			for (int i = 0;i < saved_props.length;i = i + 2) {
				objMap.put(saved_props[i], saved_props[i+1]);
			}
			//}

			System.out.println("savedProps-->"+objMap);

			try	{

				populateCommonJsonFields(cb, obj, objMap);

				obj.put("event_type", action.getActionId()); 
				obj.put("operation_type", objMap.get(MSILConstants.OPERATION_TYPE)); 
				obj.put("isNotified", objMap.get(MSILConstants.IS_NOTIFIED)); 

				obj.put("status", cb.getProps().getProperty("Status"));
				
				
				if(cb.getProps().getProperty("xtransactionid")!=null && !cb.getProps().getProperty("xtransactionid").isEmpty())
				{
			
					obj.put("transactionid", cb.getProps().getProperty("xtransactionid"));
				}else if(objMap.get("TransactionID")!=null) {
					obj.put("transactionid",objMap.get("TransactionID") );
				}
				//501 set
				if(cb.getProps().getProperty("message_id").equalsIgnoreCase("FT501C") || (cb.getProps().getProperty("message_id").equalsIgnoreCase("SSPICN1404"))
						|| (cb.getProps().getProperty("message_id").equalsIgnoreCase("SSPICN1402")) && objMap.get(MSILConstants.OPERATION_TYPE)!=null && 
						objMap.get(MSILConstants.OPERATION_TYPE).contains("AC")){

					 if (cb.getProps().getProperty("RemoteStartResult") != null) {
		                    obj.put("operation_result", (Object)cb.getProps().getProperty("RemoteStartResult"));
		                }
		                else if (cb.getProps().getProperty("AirConditioningSettingsResult") != null) {
		                    obj.put("operation_result", (Object)cb.getProps().getProperty("AirConditioningSettingsResult"));
		                }
		                else {
		                    obj.put("operation_result", (Object)cb.getProps().getProperty("RemoteStartResult"));
		                }

					if(objMap.get("ACOnStartTime")!=null)
						obj.put("ACOnStartTime", objMap.get("ACOnStartTime"));

				}else if(cb.getProps().getProperty("message_id").equalsIgnoreCase("FT501C") || (cb.getProps().getProperty("message_id").equalsIgnoreCase("SSPICN1402"))
						 && objMap.get(MSILConstants.OPERATION_TYPE)!=null && 
						objMap.get(MSILConstants.OPERATION_TYPE).contains("Defroster")) {

					 if (cb.getProps().getProperty("RemoteStartResult") != null) {
		                    obj.put("operation_result", (Object)cb.getProps().getProperty("RemoteStartResult"));
		                }
		                else if (cb.getProps().getProperty("AirConditioningSettingsResult") != null) {
		                    obj.put("operation_result", (Object)cb.getProps().getProperty("AirConditioningSettingsResult"));
		                }
		                else {
		                    obj.put("operation_result", (Object)cb.getProps().getProperty("RemoteStartResult"));
		                }

					if(objMap.get("DefrosterStartTime")!=null)
						obj.put("DefrosterStartTime", objMap.get("DefrosterStartTime"));

				}else if(cb.getProps().getProperty("message_id").equalsIgnoreCase("FT501C") || (cb.getProps().getProperty("message_id").equalsIgnoreCase("SSPICN1402")) && objMap.get(MSILConstants.OPERATION_TYPE)!=null && 
						objMap.get(MSILConstants.OPERATION_TYPE).contains("Defogger")) {

					 if (cb.getProps().getProperty("RemoteStartResult") != null) {
		                    obj.put("operation_result", (Object)cb.getProps().getProperty("RemoteStartResult"));
		                }
		                else if (cb.getProps().getProperty("AirConditioningSettingsResult") != null) {
		                    obj.put("operation_result", (Object)cb.getProps().getProperty("AirConditioningSettingsResult"));
		                }
		                else {
		                    obj.put("operation_result", (Object)cb.getProps().getProperty("RemoteStartResult"));
		                }

					if(objMap.get("DefoggerStartTime")!=null)
						obj.put("DefoggerStartTime", objMap.get("DefoggerStartTime"));

				}			
				if(cb.getProps().getProperty("message_id").equalsIgnoreCase("FT501C") ||(cb.getProps().getProperty("message_id").equalsIgnoreCase("SSPICN1402"))
						&& objMap.get(MSILConstants.OPERATION_TYPE)!=null && 
						objMap.get(MSILConstants.OPERATION_TYPE).contains("SeatVentilation")) {
					log.info("--------------------inside sendAlertNotification-------------- 286 "+obj);
					
					if(obj.has("operation_result")) {
						log.info("--------------------inside sendAlertNotification-------------- 289 "+obj);
						
					String startResult=(String) obj.get("operation_result");
					if(cb.getProps().getProperty("VentilationResult") != null) {
						log.info("--------------------inside sendAlertNotification-------------- 293 "+obj);
						
					obj.put("operation_result", startResult +":"+cb.getProps().getProperty("VentilationResult"));
					}else {
						obj.put("operation_result", startResult);
					}
					
					}else {
						 obj.put("operation_result", (Object)cb.getProps().getProperty("RemoteStartResult"));
					}
					log.info("--------------------inside sendAlertNotification-------------- 297 "+obj);
					
				}
				//401 set
				

				//209 set
				

				obj.put("returnCd", objMap.get(MSILConstants.RETURN_CODE));			

				//obj.put("message_id", cb.getProps().getProperty("MessageID"));

				obj.put("info_source", "TSC"); 		


				if(cb.getProps().getProperty("xtransactionid")!=null && !cb.getProps().getProperty("xtransactionid").isEmpty())
				{
					obj.put("transactionid", cb.getProps().getProperty("xtransactionid"));
				}else if(objMap.get("TransactionID")!=null) {
					obj.put("transactionid",objMap.get("TransactionID") );
				}

				obj.put("appRequestNo", cb.getProps().getProperty("AppRequestNo")); 

				// 603R | SVT retry

				


			}catch(JSONException e){
				//ErrorLogger.error("inside sendAlertNotification exception---->>>> for IMEI"+cb.getProps().getProperty("IMEI")+" "+e);
			}

			String msg = obj.toString();
			String imeiKey = cb.getProps().getProperty("IMEI");	
//			if(imeiKey.length() > 13)
//				imeiKey = imeiKey.substring(7, 13);
			//ErrorLogger.info("imeiKey " + imeiKey);

			System.out.println("msg--->"+msg);	


			long startTimeDMM = System.currentTimeMillis();
			//push(ALERT_TOPIC, imeiKey,msg);
			long endTimeDMM = System.currentTimeMillis();
		}
	 

		private void populateCommonJsonFields(CarProbePayload cb , JSONObject obj, HashMap<String, String> objMap) throws JSONException {
			obj.put("VIN", cb.getProps().getProperty("IMEI"));
			if(objMap !=null && objMap.get("vehicleID")!=null) {
				obj.put("contract_id",  objMap.get("vehicleID"));		
			}else {
				obj.put("contract_id",  cb.getProps().getProperty("VEHICLE_IDENTITY"));
			}
			obj.put("latitude", cb.getLatitude());
			obj.put("longitude", cb.getLongitude());
			obj.put("tscgwtime", cb.getProps().getProperty("ts"));
				obj.put("occurencetime", cb.getProps().getProperty("OccurrenceTime"));			

			
			}
}
