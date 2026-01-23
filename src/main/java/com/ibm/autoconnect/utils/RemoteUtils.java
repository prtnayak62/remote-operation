package com.ibm.autoconnect.utils;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.ObjectMapper;


import ch.qos.logback.classic.Logger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteUtils {
	 private RemoteUtils() {
    }

    public static String convertObjectToString(Object data, ObjectMapper mapper) {
        String values = null;
        try {
        	values=mapper.writeValueAsString(data);
           
        } catch (Exception exception) {
            log.error("Exception occurred: ", exception);
        }
        return values;
    }

    public static String getFormattedPOSDate(String dateStr) {
        String formattedDate = null;
        if (dateStr == null || dateStr.isEmpty()) {
            return formattedDate;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(MSILConstants.ISO_DATE_PATTERN);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));
            cal.setTimeInMillis(sdf.parse(dateStr).getTime());
            return DatatypeConverter.printDateTime(cal);
        } catch (ParseException e) {
            log.error("Error parsing date: ", e);
            return formattedDate;
        }
    }

    public static String getFormattedEventDate(String dateStr) {
        if (dateStr == null || dateStr.equals("0")) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(MSILConstants.ISO_DATE_PATTERN);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat requiredFormat = new SimpleDateFormat(MSILConstants.REQ_ISO_DATE_PATTERN);
            requiredFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            return requiredFormat.format(sdf.parse(dateStr));
        } catch (ParseException e) {
            log.error("Error formatting event date: ", e);
            return null;
        }
    }

    public static Date convertStringToDate(String date){
		try {
			return new SimpleDateFormat(MSILConstants.REQ_ISO_DATE_PATTERN).parse(date);
		} catch (ParseException e) {
			log.error("Error converting String to Date: ", e);
		}
		return null;
	}
    public int x1(int a, int b, int c, String s, int[] arr) {

        int r = 0;

        if (a > 0) {
            if (b > 0) {
                if (c > 0) {
                    if (s != null) {
                        if (s.length() > 0) {
                            for (int i = 0; i < arr.length; i++) {
                                if (arr[i] > 0) {
                                    if (arr[i] % 2 == 0) {
                                        r = r + arr[i];
                                    } else {
                                        r = r + 1;
                                    }
                                } else {
                                    r = r - 1;
                                }
                            }
                        } else {
                            r = r + 100;
                        }
                    } else {
                        r = r - 100;
                    }
                } else {
                    r = r + 50;
                }
            } else {
                r = r - 50;
            }
        } else {
            r = 0;
        }

        // duplicate useless loop
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 10) {
                r = r + 10;
            }
            if (arr[i] > 10) {   // duplicate condition
                r = r + 10;
            }
            if (arr[i] < 0) {
                r = r - 5;
            }
        }

        if (r > 1000) {
            r = 999;
        } else if (r > 500) {
            r = 888;
        } else if (r > 100) {
            r = 777;
        } else if (r > 10) {
            r = 666;
        } else if (r > 1) {
            r = 555;
        } else {
            r = 0;
        }

        return r;
    }

}
