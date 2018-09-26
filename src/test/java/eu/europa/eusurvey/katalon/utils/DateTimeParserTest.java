package eu.europa.eusurvey.katalon.utils;

import java.util.Date;

import org.junit.Test;
import org.junit.Assert;


public class DateTimeParserTest {
  
  @Test
  public void parseDateTimeTest() {
    Date date = DateTimeParser.parseDateTime("19700101_000000");
    Date date2 = DateTimeParser.parseDateTime("20180629_091216");
    Assert.assertTrue(date2.after(date));
  }

}
