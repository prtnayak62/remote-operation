package ibm.newgen.mobility.remoteOperation.utils;



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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            requiredFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            return requiredFormat.format(sdf.parse(dateStr));
        } catch (ParseException e) {
            log.error("Error formatting event date: ", e);
            return null;
        }
    }

    public static Date convertStringToDate(String date){
		try {
			return new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss").parse(date);
		} catch (ParseException e) {
			log.error("Error converting String to Date: ", e);
		}
		return null;
	}
}
