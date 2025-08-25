package com.ibm.autoconnect.model;

import java.sql.Timestamp;
import java.util.Properties;
import java.util.TimeZone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarProbe {
	public String getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public int getRegionId() {
		return regionId;
	}
	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}
	public long getMapId() {
		return mapId;
	}
	public void setMapId(long mapId) {
		this.mapId = mapId;
	}
	public String getMapVendorName() {
		return mapVendorName;
	}
	public void setMapVendorName(String mapVendorName) {
		this.mapVendorName = mapVendorName;
	}
	public String getMap_version() {
		return map_version;
	}
	public void setMap_version(String map_version) {
		this.map_version = map_version;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public int getMatchedType() {
		return matchedType;
	}
	public void setMatchedType(int matchedType) {
		this.matchedType = matchedType;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public int getInvalidMatchResult() {
		return invalidMatchResult;
	}
	public void setInvalidMatchResult(int invalidMatchResult) {
		this.invalidMatchResult = invalidMatchResult;
	}
	public double getLocRefDirection() {
		return locRefDirection;
	}
	public void setLocRefDirection(double locRefDirection) {
		this.locRefDirection = locRefDirection;
	}
	public double getLocRefDistance() {
		return locRefDistance;
	}
	public void setLocRefDistance(double locRefDistance) {
		this.locRefDistance = locRefDistance;
	}
	public String getLocRefOpenlr() {
		return locRefOpenlr;
	}
	public void setLocRefOpenlr(String locRefOpenlr) {
		this.locRefOpenlr = locRefOpenlr;
	}
	public long getMatchedMapId() {
		return matchedMapId;
	}
	public void setMatchedMapId(long matchedMapId) {
		this.matchedMapId = matchedMapId;
	}
	public String getMatchedMapVendorName() {
		return matchedMapVendorName;
	}
	public void setMatchedMapVendorName(String matchedMapVendorName) {
		this.matchedMapVendorName = matchedMapVendorName;
	}
	public String getMatchedMapVersion() {
		return matchedMapVersion;
	}
	public void setMatchedMapVersion(String matchedMapVersion) {
		this.matchedMapVersion = matchedMapVersion;
	}
	public double getRepLongitude() {
		return repLongitude;
	}
	public void setRepLongitude(double repLongitude) {
		this.repLongitude = repLongitude;
	}
	public double getRepLatitude() {
		return repLatitude;
	}
	public void setRepLatitude(double repLatitude) {
		this.repLatitude = repLatitude;
	}
	public double getRepAltitude() {
		return repAltitude;
	}
	public void setRepAltitude(double repAltitude) {
		this.repAltitude = repAltitude;
	}
	public double getRepHeading() {
		return repHeading;
	}
	public void setRepHeading(double repHeading) {
		this.repHeading = repHeading;
	}
	public int getConfidence() {
		return confidence;
	}
	public void setConfidence(int confidence) {
		this.confidence = confidence;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getHeading() {
		return heading;
	}
	public void setHeading(double heading) {
		this.heading = heading;
	}
	public long getInternalLinkId() {
		return internalLinkId;
	}
	public void setInternalLinkId(long internalLinkId) {
		this.internalLinkId = internalLinkId;
	}
	public String getExternalLinkId() {
		return externalLinkId;
	}
	public void setExternalLinkId(String externalLinkId) {
		this.externalLinkId = externalLinkId;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public Properties getStatus() {
		return status;
	}
	public void setStatus(Properties status) {
		this.status = status;
	}
	public Properties getProps() {
		return props;
	}
	public void setProps(Properties props) {
		this.props = props;
	}
	public Properties getLinkinfo() {
		return linkinfo;
	}
	public void setLinkinfo(Properties linkinfo) {
		this.linkinfo = linkinfo;
	}
	public Properties getMapmatchedAttributes() {
		return mapmatchedAttributes;
	}
	public void setMapmatchedAttributes(Properties mapmatchedAttributes) {
		this.mapmatchedAttributes = mapmatchedAttributes;
	}
	public int getVehicleStatus() {
		return vehicleStatus;
	}
	public void setVehicleStatus(int vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Timestamp getPosUpdatedTime() {
		return posUpdatedTime;
	}
	public void setPosUpdatedTime(Timestamp posUpdatedTime) {
		this.posUpdatedTime = posUpdatedTime;
	}
	public Timestamp getLastModified() {
		return lastModified;
	}
	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}
	public int getDrivingDirection() {
		return drivingDirection;
	}
	public void setDrivingDirection(int drivingDirection) {
		this.drivingDirection = drivingDirection;
	}
	public String getRoadType() {
		return roadType;
	}
	public void setRoadType(String roadType) {
		this.roadType = roadType;
	}
	public TimeZone getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	public String getCredential() {
		return credential;
	}
	public void setCredential(String credential) {
		this.credential = credential;
	}
	public double getMatchedDifference() {
		return matchedDifference;
	}
	public void setMatchedDifference(double matchedDifference) {
		this.matchedDifference = matchedDifference;
	}
	public int getINVALID_POS() {
		return INVALID_POS;
	}
	public final int INVALID_POS = -1000;
	private String vehicleId;
	private String driverId;
	private int regionId;
	private long mapId;
	private String mapVendorName;
	private String map_version;
	private String tenantId;
	private int matchedType;
	@Builder.Default
	private double longitude = -1000.0;
	@Builder.Default
	private double latitude = -1000.0;
	private double altitude;
	private int invalidMatchResult;
	private double locRefDirection;
	private double locRefDistance;
	private String locRefOpenlr;
	private long matchedMapId;
	private String matchedMapVendorName;
	private String matchedMapVersion;
	@Builder.Default
	private double repLongitude = -1000.0;
	@Builder.Default
	private double repLatitude = -1000.0;
	private double repAltitude;
	@Builder.Default
	private double repHeading = -1000.0;
	@Builder.Default
	private int confidence = 5;
	@Builder.Default
	private double speed = -1000.0;
	@Builder.Default
	private double heading = -1000.0;
	@Builder.Default
	private long internalLinkId = -1L;
	private String externalLinkId;
	private double distance;
	private Properties status;
	private Properties props;
	private Properties linkinfo;
	private Properties mapmatchedAttributes;
	@Builder.Default
	private int vehicleStatus = -1;
	private String sessionId;
	private Timestamp posUpdatedTime;
	private Timestamp lastModified;
	private int drivingDirection;
	private String roadType;
	@Builder.Default
	private TimeZone timeZone = TimeZone.getTimeZone("UTC");
	private String credential;
	private double matchedDifference;
}