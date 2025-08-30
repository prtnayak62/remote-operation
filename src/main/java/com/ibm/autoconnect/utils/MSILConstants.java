/************************************************************************
 * Licensed Materials - Property of IBM
 *
 * (C) Copyright IBM Corp. 2020  All Rights Reserved.
 *
 * US Government Users Restricted Rights - Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ************************************************************************/
package com.ibm.autoconnect.utils;

public class MSILConstants {

	public static final String RETURN_CODE     = "ReturnCd";

	public static final String IS_NOTIFIED     = "IsNotified";
	public static final String OPERATION_TYPE     = "OperationType";

	public static final String ISO_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	//added by Neeru for Required DateTime Format
	public static final String REQ_ISO_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	
	public static final String REMOTECALLRESET_ACTION = "SEND_REMOTERESULT";
	
	public static final String REMOTERESULT_EVENT     = "FT501C";

	public static final String REMOTERESULTGEN3_EVENT     = "SSPICN1402";
	public static final String couchbaseScope     = "core";
	public static final String PROBECOLLECTIONCOUCH     = "car-probe";
	public static final String VASTATE     = "va-state";
	public static final String REMOTEOPS     = "remoteops";
	
	
	public enum REMOTEOP {
		ACEngineON,DefrosterEngineON,DefoggerEngineON,
		ACON,ACOFF,DefrosterON,DefrosterOFF,DefoggerON,DefoggerOFF,ACEngineOFF,DefrosterEngineOFF,FrontDriverSeatVentilationON,FrontDriverSeatVentilationOFF,
		FrontPassengerSeatVentilationON,FrontPassengerSeatVentilationOFF,DefoggerEngineOFF,ACTempChange,
		SeatVentilationON,SeatVentilationOFF,FrontPassengerSeatVentilation,FrontDriverSeatVentilation,RearDriverSeatVentilationON,RearDriverSeatVentilationOFF,
		RearPassengerSeatVentilationON,RearPassengerSeatVentilationOFF}


}
