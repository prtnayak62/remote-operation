package com.ibm.autoconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class DCMPositionAccuracy
{
	
	@JsonProperty("MajorAxisError")
    private Integer MajorAxisError;

	@JsonProperty("MinorAxisError")
    private Integer MinorAxisError;

	public Integer getMajorAxisError() {
		return MajorAxisError;
	}

	public void setMajorAxisError(Integer majorAxisError) {
		MajorAxisError = majorAxisError;
	}

	public Integer getMinorAxisError() {
		return MinorAxisError;
	}

	public void setMinorAxisError(Integer minorAxisError) {
		MinorAxisError = minorAxisError;
	}

 
}

