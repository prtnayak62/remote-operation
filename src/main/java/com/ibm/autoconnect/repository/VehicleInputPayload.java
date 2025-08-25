package com.ibm.autoconnect.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Data
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleInputPayload {

	@JsonProperty(value = "message_id")
	private String messageId;
	
	@JsonProperty(value = "Vin")
	private String vin;
	

	
	@JsonProperty("x-transactionid")
	private String xtransactionid;
	
	

	@JsonProperty("tscgwtime")
	private String tscgwtime;

	@JsonProperty("DataCreationTime")
	private String DataCreationTime;
	
	@JsonProperty("Correlation-id")
    private String Correlation_id;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getTscgwtime() {
		return tscgwtime;
	}

	public void setTscgwtime(String tscgwtime) {
		this.tscgwtime = tscgwtime;
	}

	public String getXtransactionid() {
		return xtransactionid;
	}

	public void setXtransactionid(String xtransactionid) {
		this.xtransactionid = xtransactionid;
	}

	public String getDataCreationTime() {
		return DataCreationTime;
	}

	public void setDataCreationTime(String dataCreationTime) {
		DataCreationTime = dataCreationTime;
	}

	public String getCorrelation_id() {
		return Correlation_id;
	}

	public void setCorrelation_id(String correlation_id) {
		Correlation_id = correlation_id;
	}
	 
}
