package com.ibm.autoconnect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoteResponseModel {
    
	private String result;
    private int statusCode;
    private String message;
	
}
