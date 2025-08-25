package com.ibm.autoconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;




@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionInfo3_0
{

	@JsonProperty("Longitude")
	private Double Longitude;

	@JsonProperty("Latitude")
	private Double Latitude;

	@JsonProperty("PositionValidFlag")
	private boolean positionValidFlag;
	
	@JsonProperty("AcquisitionDateTime")
	private String acquisitionDateTime;
	
	@JsonProperty("GeodeticReferenceSystem")
	private String geodeticReferenceSystem;
	
	@JsonProperty("MajorAxisError")
	private int majorAxisError;
	
	@JsonProperty("MinorAxisError")
	private int minorAxisError;
	
	@JsonProperty("MajorAxisGradient")
	private int majorAxisGradient;
	
	@JsonProperty("Heading")
	private int heading;
	
	@JsonProperty("Altitude")
	private double altitude;
	
	@JsonProperty("GeoidHeight")
	private double geoidHeight;

	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return Longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Double longitude) {
		Longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public Double getLatitude() {
		return Latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Double latitude) {
		Latitude = latitude;
	}

	/**
	 * @return the positionValidFlag
	 */
	public boolean isPositionValidFlag() {
		return positionValidFlag;
	}

	/**
	 * @param positionValidFlag the positionValidFlag to set
	 */
	public void setPositionValidFlag(boolean positionValidFlag) {
		this.positionValidFlag = positionValidFlag;
	}

	/**
	 * @return the acquisitionDateTime
	 */
	public String getAcquisitionDateTime() {
		return acquisitionDateTime;
	}

	/**
	 * @param acquisitionDateTime the acquisitionDateTime to set
	 */
	public void setAcquisitionDateTime(String acquisitionDateTime) {
		this.acquisitionDateTime = acquisitionDateTime;
	}

	/**
	 * @return the geodeticReferenceSystem
	 */
	public String getGeodeticReferenceSystem() {
		return geodeticReferenceSystem;
	}

	/**
	 * @param geodeticReferenceSystem the geodeticReferenceSystem to set
	 */
	public void setGeodeticReferenceSystem(String geodeticReferenceSystem) {
		this.geodeticReferenceSystem = geodeticReferenceSystem;
	}

	/**
	 * @return the majorAxisError
	 */
	public int getMajorAxisError() {
		return majorAxisError;
	}

	/**
	 * @param majorAxisError the majorAxisError to set
	 */
	public void setMajorAxisError(int majorAxisError) {
		this.majorAxisError = majorAxisError;
	}

	/**
	 * @return the minorAxisError
	 */
	public int getMinorAxisError() {
		return minorAxisError;
	}

	/**
	 * @param minorAxisError the minorAxisError to set
	 */
	public void setMinorAxisError(int minorAxisError) {
		this.minorAxisError = minorAxisError;
	}

	/**
	 * @return the majorAxisGradient
	 */
	public int getMajorAxisGradient() {
		return majorAxisGradient;
	}

	/**
	 * @param majorAxisGradient the majorAxisGradient to set
	 */
	public void setMajorAxisGradient(int majorAxisGradient) {
		this.majorAxisGradient = majorAxisGradient;
	}

	/**
	 * @return the heading
	 */
	public int getHeading() {
		return heading;
	}

	/**
	 * @param heading the heading to set
	 */
	public void setHeading(int heading) {
		this.heading = heading;
	}

	/**
	 * @return the altitude
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * @param altitude the altitude to set
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	/**
	 * @return the geoidHeight
	 */
	public double getGeoidHeight() {
		return geoidHeight;
	}

	/**
	 * @param geoidHeight the geoidHeight to set
	 */
	public void setGeoidHeight(double geoidHeight) {
		this.geoidHeight = geoidHeight;
	}


	
}
