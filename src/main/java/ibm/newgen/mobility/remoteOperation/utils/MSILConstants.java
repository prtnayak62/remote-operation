/************************************************************************
 * Licensed Materials - Property of IBM
 *
 * (C) Copyright IBM Corp. 2020  All Rights Reserved.
 *
 * US Government Users Restricted Rights - Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ************************************************************************/
package ibm.newgen.mobility.remoteOperation.utils;

public class MSILConstants {


	public static final String ISO_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	//added by Neeru for Required DateTime Format
	public static final String REQ_ISO_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	

	public static final String REMOTERESULT_EVENT     = "FT501C";

	public static final String REMOTERESULTGEN3_EVENT     = "SSPICN1402";
	
	public enum REMOTEOPS {
		ACEngineON,DefrosterEngineON,DefoggerEngineON,
		ACON,ACOFF,DefrosterON,DefrosterOFF,DefoggerON,DefoggerOFF,ACEngineOFF,DefrosterEngineOFF,FrontDriverSeatVentilationON,FrontDriverSeatVentilationOFF,
		FrontPassengerSeatVentilationON,FrontPassengerSeatVentilationOFF,DefoggerEngineOFF,ACTempChange,
		SeatVentilationON,SeatVentilationOFF,FrontPassengerSeatVentilation,FrontDriverSeatVentilation,RearDriverSeatVentilationON,RearDriverSeatVentilationOFF,
		RearPassengerSeatVentilationON,RearPassengerSeatVentilationOFF}


}
