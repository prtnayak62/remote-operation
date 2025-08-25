package com.ibm.autoconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties( ignoreUnknown = true )
public class PositionInfo
{
	
	@JsonProperty("AcquisitionDatetime")
    private String AcquisitionDatetime;

	@JsonProperty("Longitude")
    private Double Longitude;

	@JsonProperty("Latitude")
    private Double Latitude;
	
	@JsonProperty("GeodeticReferenceSystem")
    private Integer GeodeticReferenceSystem;

	@JsonProperty("DcmPositionAccuracy")
    private DCMPositionAccuracy DcmPositionAccuracy;
	
	@JsonProperty("PositionValidFlag") 
	private boolean positionValidFlag;
	
	@JsonProperty("MajorAxisError") 
	private int majorAxisError;
    @JsonProperty("MinorAxisError") 
    private int minorAxisError;
    @JsonProperty("MajorAxisGradient") 
    private int majorAxisGradient;
    @JsonProperty("Heading") 
    private int heading;
    @JsonProperty("Altitude") 
    private int altitude;
    @JsonProperty("GeoidHeight") 
    private double geoidHeight;

    public void setAcquisitionDatetime(String AcquisitionDatetime){
        this.AcquisitionDatetime = AcquisitionDatetime;
    }
    public String getAcquisitionDatetime(){
        return this.AcquisitionDatetime;
    }
  
    public void setDcmPositionAccuracy(DCMPositionAccuracy DcmPositionAccuracy){
        this.DcmPositionAccuracy = DcmPositionAccuracy;
    }
    public DCMPositionAccuracy getDcmPositionAccuracy(){
        return this.DcmPositionAccuracy;
    }
	public Double getLongitude() {
		return Longitude;
	}
	public void setLongitude(Double longitude) {
		Longitude = longitude;
	}
	public Double getLatitude() {
		return Latitude;
	}
	public void setLatitude(Double latitude) {
		Latitude = latitude;
	}
	
	public Integer getGeodeticReferenceSystem() {
		return GeodeticReferenceSystem;
	}
	public void setGeodeticReferenceSystem(Integer geodeticReferenceSystem) {
		GeodeticReferenceSystem = geodeticReferenceSystem;
	}
	public boolean isPositionValidFlag() {
		return positionValidFlag;
	}
	public void setPositionValidFlag(boolean positionValidFlag) {
		this.positionValidFlag = positionValidFlag;
	}
	public int getMajorAxisError() {
		return majorAxisError;
	}
	public void setMajorAxisError(int majorAxisError) {
		this.majorAxisError = majorAxisError;
	}
	public int getMinorAxisError() {
		return minorAxisError;
	}
	public void setMinorAxisError(int minorAxisError) {
		this.minorAxisError = minorAxisError;
	}
	public int getMajorAxisGradient() {
		return majorAxisGradient;
	}
	public void setMajorAxisGradient(int majorAxisGradient) {
		this.majorAxisGradient = majorAxisGradient;
	}
	public int getHeading() {
		return heading;
	}
	public void setHeading(int heading) {
		this.heading = heading;
	}
	public int getAltitude() {
		return altitude;
	}
	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}
	public double getGeoidHeight() {
		return geoidHeight;
	}
	public void setGeoidHeight(double geoidHeight) {
		this.geoidHeight = geoidHeight;
	}
	
	
	
}
