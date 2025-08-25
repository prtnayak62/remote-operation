package com.ibm.autoconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author SoniaAhlawat
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class RemoteControlTimeInfo {

	@JsonProperty("CenterTimeStamp")	
	private String CenterTimeStamp;

	@JsonProperty("RequestReceptionTime")
	private String RequestReceptionTime;

	@JsonProperty("OperationEndTime")
	private String OperationEndTime;

	@JsonProperty("StandbyStartTime")
	private String StandbyStartTime;

	public void setCenterTimeStamp(String CenterTimeStamp){
		this.CenterTimeStamp = CenterTimeStamp;
	}
	public String getCenterTimeStamp(){
		return this.CenterTimeStamp;
	}
	public void setRequestReceptionTime(String RequestReceptionTime){
		this.RequestReceptionTime = RequestReceptionTime;
	}
	public String getRequestReceptionTime(){
		return this.RequestReceptionTime;
	}
	public void setOperationEndTime(String OperationEndTime){
		this.OperationEndTime = OperationEndTime;
	}
	public String getOperationEndTime(){
		return this.OperationEndTime;
	}
	public void setStandbyStartTime(String StandbyStartTime){
		this.StandbyStartTime = StandbyStartTime;
	}
	public String getStandbyStartTime(){
		return this.StandbyStartTime;
	}

}
