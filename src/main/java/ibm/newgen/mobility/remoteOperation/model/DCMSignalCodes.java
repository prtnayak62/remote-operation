package ibm.newgen.mobility.remoteOperation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DCMSignalCodes {

	public Integer getCalidStatus() {
		return calidStatus;
	}

	public void setCalidStatus(Integer calidStatus) {
		this.calidStatus = calidStatus;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getMsec() {
		return msec;
	}

	public void setMsec(Integer msec) {
		this.msec = msec;
	}

	@JsonProperty("ValidStatus")
	private Integer calidStatus;

	@JsonProperty("Code")
	private String code;
	
	@JsonProperty("Symbol")
	private String symbol;

	@JsonProperty("Value")
	private Double value;

	@JsonProperty("msec")
	private Integer msec;
}
