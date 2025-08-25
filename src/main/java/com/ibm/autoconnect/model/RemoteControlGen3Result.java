
package com.ibm.autoconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.autoconnect.repository.VehicleInputPayload;




/**
 * @author SoniaAhlawat
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class RemoteControlGen3Result extends VehicleInputPayload {

	@JsonProperty("PositionInfo")
	private PositionInfo3_0 PositionInfo;

	@JsonProperty("Correlation-id")
	private String Correlation_id;

	@JsonProperty("IsInServiceMode")
	private String IsInServiceMode;

	@JsonProperty("RequestType")
	private String requestType;

	@JsonProperty("RequestStatus")
	private String requestStatus;

	@JsonProperty("ResultCode")
	private String resultCode;

	@JsonProperty("CenterTimestamp")
	private String centerTimestamp;

	@JsonProperty("RequestReceptionTime")
	private String requestReceptionTime;

	@JsonProperty("OperationEndTime")
	private String operationEndTime;

	@JsonProperty("DataCreationTime")
	private String dataCreationTime;

//    @JsonProperty("PositionInfo") 
//    private PositionInfo positionInfo;

	@JsonProperty("RemoteClimateStatus")
	private String remoteClimateStatus;

	@JsonProperty("BatteryPreconStatus")
	private String batteryPreconStatus;

	@JsonProperty("tscgwtime")
	private String tscgwtime;

	@JsonProperty("RemoteClimateStatus")
	private String RemoteClimateStatus;

	@JsonProperty("RemoteClimateStopInfo")
	private String RemoteClimateStartStopInfo;

	@JsonProperty("ReservationNo")
	private String ReservationNo;

	public PositionInfo3_0 getPositionInfo() {
		return PositionInfo;
	}

	public void setPositionInfo(PositionInfo3_0 positionInfo) {
		PositionInfo = positionInfo;
	}

	public String getCorrelation_id() {
		return Correlation_id;
	}

	public void setCorrelation_id(String correlation_id) {
		Correlation_id = correlation_id;
	}

	public String getIsInServiceMode() {
		return IsInServiceMode;
	}

	public void setIsInServiceMode(String isInServiceMode) {
		IsInServiceMode = isInServiceMode;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getCenterTimestamp() {
		return centerTimestamp;
	}

	public void setCenterTimestamp(String centerTimestamp) {
		this.centerTimestamp = centerTimestamp;
	}

	public String getRequestReceptionTime() {
		return requestReceptionTime;
	}

	public void setRequestReceptionTime(String requestReceptionTime) {
		this.requestReceptionTime = requestReceptionTime;
	}

	public String getOperationEndTime() {
		return operationEndTime;
	}

	public void setOperationEndTime(String operationEndTime) {
		this.operationEndTime = operationEndTime;
	}

	public String getDataCreationTime() {
		return dataCreationTime;
	}

	public void setDataCreationTime(String dataCreationTime) {
		this.dataCreationTime = dataCreationTime;
	}

//	public PositionInfo getPositionInfo() {
//		return positionInfo;
//	}
//
//	public void setPositionInfo(PositionInfo positionInfo) {
//		this.positionInfo = positionInfo;
//	}

	/*
	 * public String getRemoteClimateStatus() { return remoteClimateStatus; }
	 * 
	 * public void setRemoteClimateStatus(String remoteClimateStatus) {
	 * this.remoteClimateStatus = remoteClimateStatus; }
	 */

	public String getBatteryPreconStatus() {
		return batteryPreconStatus;
	}

	public void setBatteryPreconStatus(String batteryPreconStatus) {
		this.batteryPreconStatus = batteryPreconStatus;
	}

	public String getTscgwtime() {
		return tscgwtime;
	}

	public void setTscgwtime(String tscgwtime) {
		this.tscgwtime = tscgwtime;
	}

	public String getRemoteClimateStatus() {
		return RemoteClimateStatus;
	}

	public void setRemoteClimateStatus(String remoteClimateStatus) {
		RemoteClimateStatus = remoteClimateStatus;
	}

	public String getRemoteClimateStartStopInfo() {
		return RemoteClimateStartStopInfo;
	}

	public void setRemoteClimateStartStopInfo(String remoteClimateStartStopInfo) {
		RemoteClimateStartStopInfo = remoteClimateStartStopInfo;
	}

	public String getReservationNo() {
		return ReservationNo;
	}

	public void setReservationNo(String reservationNo) {
		ReservationNo = reservationNo;
	}

}
