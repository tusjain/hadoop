package com.kashit.traffic;

import java.util.Calendar;

public class TimeUtil {
  /**
   * Parse a string that looks like 01/01/2012 00:00:00.
   * Returns an integer between 0 and 604,800, the number of seconds in a week.
   */
  public static int toTimeOfWeek(String dateTime) {
    String[] tokens = dateTime.split(" ");
    String date = tokens[0];
    String time = tokens[1];
    String[] dateTokens = date.split("/");
    String[] timeTokens = time.split(":");
    
    Calendar calendar = Calendar.getInstance();
    calendar.set(Integer.parseInt(dateTokens[2]), Integer.parseInt(dateTokens[0])-1, Integer.parseInt(dateTokens[1]));
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    
    int daySeconds = Integer.parseInt(timeTokens[0]) * 60 * 60 + Integer.parseInt(timeTokens[1]) * 60 + Integer.parseInt(timeTokens[2]);
    return dayOfWeek * 60 * 60 * 24 + daySeconds;
  }
  
  public static long toTime(String dateTime) {
    String[] tokens = dateTime.split(" ");
    String date = tokens[0];
    String time = tokens[1];
    String[] dateTokens = date.split("/");
    String[] timeTokens = time.split(":");
    
    Calendar calendar = Calendar.getInstance();
    calendar.set(Integer.parseInt(dateTokens[2]), Integer.parseInt(dateTokens[0])-1,
        Integer.parseInt(dateTokens[1]), Integer.parseInt(timeTokens[0]),
        Integer.parseInt(timeTokens[1]), Integer.parseInt(timeTokens[2]));
    
    return calendar.getTimeInMillis();
  }
}