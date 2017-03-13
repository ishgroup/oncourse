package ish.oncourse.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

	public static Date startOfMonth(Date month) {
		Calendar startOfMonth = Calendar.getInstance();
		startOfMonth.setTime(month);
		startOfMonth.set(Calendar.DAY_OF_MONTH, startOfMonth.getActualMinimum(Calendar.DAY_OF_MONTH));
		return startOfMonth.getTime();
	}

	public static Date endOfMonth(Date month) {
		Calendar endOfMonth = Calendar.getInstance();
		endOfMonth.setTime(month);
		endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
		endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
		endOfMonth.set(Calendar.MINUTE, 59);
		endOfMonth.set(Calendar.SECOND, 59);
		return endOfMonth.getTime();
	}

	public static boolean isCurrentMonth(Date month) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM");
		return fmt.format(month).equals(fmt.format(new Date()));
	}

	public static Date startOfDay(Date day) {
		return startOfDay(day, null);
	}
	
	public static Date startOfDay(Date day, String timeZone) {
		Calendar startOfDay = Calendar.getInstance();
		startOfDay.setTimeZone(timeZone == null ? TimeZone.getDefault() :TimeZone.getTimeZone(timeZone));
		startOfDay.setTime(day);
		startOfDay.set(Calendar.HOUR_OF_DAY, 0);
		startOfDay.set(Calendar.MINUTE, 0);
		startOfDay.set(Calendar.SECOND, 0);
		return startOfDay.getTime();
	}

	public static Date endOfDay(Date day) {
		Calendar startOfDay = Calendar.getInstance();
		startOfDay.setTime(day);
		startOfDay.set(Calendar.HOUR_OF_DAY, 23);
		startOfDay.set(Calendar.MINUTE, 59);
		startOfDay.set(Calendar.SECOND, 59);
		return startOfDay.getTime();
	}
}
