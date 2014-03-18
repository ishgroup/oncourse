package ish.util;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Set of basic, utility methods for java.util.Date and java.util.Calendar classes. Sometimes wonder why those methods are not a part of java itself.
 * 
 * @author marcin
 */
public final class DateTimeUtil {

	private static final double MILISECONDS_PER_DAY = 24 * 60 * 60 * 1000d;

	private DateTimeUtil() {}

	/**
	 * Adds a day to the Date, daylight savings safe.
	 * 
	 * @param initialDate
	 * @param days to add
	 * @return new Date object after adding days
	 */
	public static Date addDaysDaylightSafe(Date initialDate, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(initialDate);

		// need to store the fields to avoid problems with daylight savings
		int startHour = calendar.get(Calendar.HOUR_OF_DAY);
		int startMinute = calendar.get(Calendar.MINUTE);
		int startSecond = calendar.get(Calendar.SECOND);
		int startMilisecond = calendar.get(Calendar.MILLISECOND);

		calendar.add(Calendar.DATE, days);

		calendar.set(Calendar.HOUR_OF_DAY, startHour);
		calendar.set(Calendar.MINUTE, startMinute);
		calendar.set(Calendar.SECOND, startSecond);
		calendar.set(Calendar.MILLISECOND, startMilisecond);

		return calendar.getTime();
	}

	/**
	 * returns number of days between two dates.
	 * 
	 * @param startDate
	 * @param endDate
	 * @return number of days
	 */
	public static int getDaysLeapYearDaylightSafe(Date startDate, Date endDate) {
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		start.set(Calendar.HOUR_OF_DAY, 12);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);

		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		end.set(Calendar.HOUR_OF_DAY, 12);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);

		double result = (end.getTimeInMillis() - start.getTimeInMillis()) / MILISECONDS_PER_DAY;

		return (int) Math.round(result);
	}

	public static Date getFinancialYearStart() {
		Calendar gc = Calendar.getInstance();
		if (gc.get(Calendar.MONTH) < Calendar.JULY) {
			gc.add(Calendar.YEAR , -1);
		}
		gc.set(Calendar.MONTH, Calendar.JULY);
		gc.set(Calendar.DAY_OF_MONTH, 1);
		return trancateToMidnight(gc.getTime());
	}

	public static Date getFirstDayOfLastMonth() {
		Calendar gc = Calendar.getInstance();
		gc.add(Calendar.MONTH, -1);
		gc.set(Calendar.DAY_OF_MONTH, 1);
		return trancateToMidnight(gc.getTime());
	}

	public static Date trancateToMidnight(Date date) {
		return DateUtils.truncate(date, Calendar.DATE);
	}

	public static Date getFirstDayOfCurrentMonth() {
		Calendar gc = Calendar.getInstance();
		gc.set(Calendar.DAY_OF_MONTH, 1);
		return trancateToMidnight(gc.getTime());
	}
}
