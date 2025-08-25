package com.ibm.autoconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author SoniaAhlawat
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class ReqestResultStatus {

	@JsonProperty("Status")
	private Integer Status;

	@JsonProperty("Phase")
	private Integer Phase;

	@JsonProperty("SubsequenceCount")
	private String SubsequenceCount;

	public Integer getStatus() {
		return Status;
	}

	public void setStatus(Integer status) {
		Status = status;
	}

	public Integer getPhase() {
		return Phase;
	}

	public void setPhase(Integer phase) {
		Phase = phase;
	}

	/**
	 * @return the subsequenceCount
	 */
	public String getSubsequenceCount() {
		return SubsequenceCount;
	}

	/**
	 * @param subsequenceCount the subsequenceCount to set
	 */
	public void setSubsequenceCount(String subsequenceCount) {
		SubsequenceCount = subsequenceCount;
	}



}
