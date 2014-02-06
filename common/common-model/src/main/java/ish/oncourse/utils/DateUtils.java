package ish.oncourse.utils;

import java.util.Calendar;

public class DateUtils {
	
	public static boolean isTheSameTime(Calendar cal1, Calendar cal2) {
    	if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
    	if (cal1.getTimeZone().equals(cal2.getTimeZone())) {
    		return (cal1.get(Calendar.MILLISECOND) == cal2.get(Calendar.MILLISECOND) &&
                    cal1.get(Calendar.SECOND) == cal2.get(Calendar.SECOND) &&
                    cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE) &&
                    cal1.get(Calendar.HOUR) == cal2.get(Calendar.HOUR) &&
                    cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                    cal1.getClass() == cal2.getClass());
    	}
    	//return false for check in different timezones
    	return false;
    }
}
