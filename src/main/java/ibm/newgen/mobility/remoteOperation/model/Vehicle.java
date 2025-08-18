package ibm.newgen.mobility.remoteOperation.model;

import java.sql.Timestamp;
import java.util.Properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Vehicle {
	public String getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	public int getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(int vehicleType) {
		this.vehicleType = vehicleType;
	}
	public int getVehicleUsage() {
		return vehicleUsage;
	}
	public void setVehicleUsage(int vehicleUsage) {
		this.vehicleUsage = vehicleUsage;
	}
	public int getVehicleWidth() {
		return vehicleWidth;
	}
	public void setVehicleWidth(int vehicleWidth) {
		this.vehicleWidth = vehicleWidth;
	}
	public int getVehicleHeight() {
		return vehicleHeight;
	}
	public void setVehicleHeight(int vehicleHeight) {
		this.vehicleHeight = vehicleHeight;
	}
	public int getVehicleStatus() {
		return vehicleStatus;
	}
	public void setVehicleStatus(int vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}
	public String getVehicleModel() {
		return vehicleModel;
	}
	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}
	public String getVehicleSerialNumber() {
		return vehicleSerialNumber;
	}
	public void setVehicleSerialNumber(String vehicleSerialNumber) {
		this.vehicleSerialNumber = vehicleSerialNumber;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
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
	public String getMapVersion() {
		return mapVersion;
	}
	public void setMapVersion(String mapVersion) {
		this.mapVersion = mapVersion;
	}
	public Properties getProps() {
		return props;
	}
	public void setProps(Properties props) {
		this.props = props;
	}
	public Timestamp getLastModified() {
		return lastModified;
	}
	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFwVersion() {
		return fwVersion;
	}
	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}
	public String getHwVersion() {
		return hwVersion;
	}
	public void setHwVersion(String hwVersion) {
		this.hwVersion = hwVersion;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public String getAssetNum() {
		return assetNum;
	}
	public void setAssetNum(String assetNum) {
		this.assetNum = assetNum;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public static int getAnonymousVehicle() {
		return ANONYMOUS_VEHICLE;
	}
	private String vehicleId;
	private int vehicleType;
	private int vehicleUsage;
	private int vehicleWidth;
	private int vehicleHeight;
	private int vehicleStatus;
	private String vehicleModel;
	private String vehicleSerialNumber;
	private String driverId;
	private String tenantId;
	private int regionId;
	private long mapId;
	private String mapVendorName;
	private String mapVersion;
	private Properties props;
	private Timestamp lastModified;
	private String status;
	private String fwVersion;
	private String hwVersion;
	private String assetType;
	private String assetNum;
	private String siteId;
	public static final int ANONYMOUS_VEHICLE = -1;
}