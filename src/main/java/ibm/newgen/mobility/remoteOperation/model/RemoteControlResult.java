/**
 * 
 */
package ibm.newgen.mobility.remoteOperation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SoniaAhlawat
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteControlResult {

	@JsonProperty("OccurrenceTime")
	private String OccurrenceTime;

	@JsonProperty("DcmDormantDatetime")
	private String DcmDormantDatetime;

	@JsonProperty("PositionInfo")
	private PositionInfo PositionInfo;

	@JsonProperty("TimeInformation")
	private RemoteControlTimeInfo TimeInformation;

	@JsonProperty("VehicleFinderResult")
	private String VehicleFinderResult;

	@JsonProperty("HazardResult")
	private String HazardResult;

	
	
	@JsonProperty("RemoteStartResult")
	private String RemoteStartResult;
	@JsonProperty("AirConditioningSettingsResult")
	private String AirConditioningSettingsResult;
	
	@JsonProperty("VentilationResult")
	private String VentilationResult;
	
	
	@JsonProperty("Destination")
	private Double Destination;
	
	
	@JsonProperty("RemoteHvacResultParameter1")
	private String RemoteHvacResultParameter1;
	
	
	@JsonProperty("RemoteHvacResultParameter2")
	private String RemoteHvacResultParameter2;

	public String getRemoteHvacResultParameter1() {
		return RemoteHvacResultParameter1;
	}
	public void setRemoteHvacResultParameter1(String remoteHvacResultParameter1) {
		RemoteHvacResultParameter1 = remoteHvacResultParameter1;
	}
	public String getRemoteHvacResultParameter2() {
		return RemoteHvacResultParameter2;
	}
	public void setRemoteHvacResultParameter2(String remoteHvacResultParameter2) {
		RemoteHvacResultParameter2 = remoteHvacResultParameter2;
	}
	public String getAirConditioningSettingsResult() {
		return AirConditioningSettingsResult;
	}
	public void setAirConditioningSettingsResult(String airConditioningSettingsResult) {
		AirConditioningSettingsResult = airConditioningSettingsResult;
	}
	public String getVentilationResult() {
		return VentilationResult;
	}
	public void setVentilationResult(String ventilationResult) {
		VentilationResult = ventilationResult;
	}
	public void setOccurrenceTime(String OccurrenceTime){
		this.OccurrenceTime = OccurrenceTime;
	}
	public String getOccurrenceTime(){
		return this.OccurrenceTime;
	}
	public void setDcmDormantDatetime(String DcmDormantDatetime){
		this.DcmDormantDatetime = DcmDormantDatetime;
	}
	public String getDcmDormantDatetime(){
		return this.DcmDormantDatetime;
	}
	public void setPositionInfo(PositionInfo PositionInfo){
		this.PositionInfo = PositionInfo;
	}
	public PositionInfo getPositionInfo(){
		return this.PositionInfo;
	}
	public void setTimeInformation(RemoteControlTimeInfo TimeInformation){
		this.TimeInformation = TimeInformation;
	}
	public RemoteControlTimeInfo getTimeInformation(){
		return this.TimeInformation;
	}
	public void setVehicleFinderResult(String VehicleFinderResult){
		this.VehicleFinderResult = VehicleFinderResult;
	}
	public String getVehicleFinderResult(){
		return this.VehicleFinderResult;
	}
	public void setHazardResult(String HazardResult){
		this.HazardResult = HazardResult;
	}
	public String getHazardResult(){
		return this.HazardResult;
	}
	

	/**
	 * @return the remoteStartResult
	 */
	public String getRemoteStartResult() {
		return RemoteStartResult;
	}
	/**
	 * @param remoteStartResult the remoteStartResult to set
	 */
	public void setRemoteStartResult(String remoteStartResult) {
		RemoteStartResult = remoteStartResult;
	}
	public Double getDestination() {
		return Destination;
	}
	public void setDestination(Double destination) {
		Destination = destination;
	}
	
	
	

}
