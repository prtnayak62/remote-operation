/**
 * 
 */
package com.ibm.autoconnect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.autoconnect.repository.VehicleInputPayload;





/**
 * @author SoniaAhlawat
 *
 */

@JsonIgnoreProperties( ignoreUnknown = true )
public class RemoteControlCallback extends VehicleInputPayload {

	
	@JsonProperty("x-transactionid")
	private String xtransactionid;
	
	@JsonProperty("AppRequestNo")
	private String AppRequestNo;

	
	@JsonProperty("RequestResult")
	private ReqestResultStatus RequestResult;
	@JsonProperty("RemoteControlResult")
	private RemoteControlResult RemoteControlResult;
	
	
	

	public void setAppRequestNo(String AppRequestNo){
		this.AppRequestNo = AppRequestNo;
	}
	public String getAppRequestNo(){
		return this.AppRequestNo;
	}
	
	public void setRemoteControlResult(RemoteControlResult RemoteControlResult){
		this.RemoteControlResult = RemoteControlResult;
	}
	public RemoteControlResult getRemoteControlResult(){
		return this.RemoteControlResult;
	}
	
	public String getXtransactionid() {
		return xtransactionid;
	}
	public void setXtransactionid(String xtransactionid) {
		this.xtransactionid = xtransactionid;
	}
	public ReqestResultStatus getRequestResult() {
		return RequestResult;
	}
	public void setRequestResult(ReqestResultStatus requestResult) {
		RequestResult = requestResult;
	}
	
}
