package eu.europa.eusurvey.katalon.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeParser {  
  private DateTimeParser() {
    
  }
  public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

  public static Date parseDateTime(String dateStr) {
    try {
      return DATE_TIME_FORMAT.parse(dateStr);
    } catch (ParseException e) {
      return null;
    }
  }

}
