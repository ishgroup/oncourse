/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.util;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A formatter which understands complex date entry options
 */
public class DateFormatter extends DefaultFormatter {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger();
	private static final long ONE_DAY_MILLISECONDS = 24 * 60 * 60 * 1000L;
	/*
	 * For the parser we need to split the input string around most punctuation: " ", "/", "\", ":", ".", ",", "|"
	 * We also match "-" but only if it is not following a space since that means that it should instead be
	 * interpreted as an adjustment value. For example "3-5-2004 -10d" means "3 May 2004 minus 10 days".
	 * To achieve this we use the regex negative lookbehind pattern "(?<!a)b" meaning 'match b,
	 * but only if it doesn't follow a'.
	 */
	private static final Pattern DATE_PARSE_PTN = Pattern.compile("[\\s\\\\/:.,|]|(?<!\\s)\\-"); // whitespace \ / : . , |

	private static final DateFormatSymbols formatData = new DateFormatSymbols(Locale.getDefault());
	private static List<String> shortWeekdays = Arrays.asList(formatData.getShortWeekdays());
	private static List<String> longWeekdays = Arrays.asList(formatData.getWeekdays());
	private static List<String> shortMonths = Arrays.asList(formatData.getShortMonths());
	private static List<String> longMonths = Arrays.asList(formatData.getMonths());


	public static SimpleDateFormat FORMAT_DEFAULT = new SimpleDateFormat("EEE d MMM yyyy");
	public static SimpleDateFormat FORMAT_CURRENT_YEAR = new SimpleDateFormat("EEE d MMM");

	public static SimpleDateFormat FORMAT_DATE_DD_MMM_YYYY = new SimpleDateFormat("dd MMM yyyy");
	public static SimpleDateFormat FORMAT_DATE_DD_MMM_YY = new SimpleDateFormat("dd MMM yy");
	public static SimpleDateFormat FORMAT_DATE_YYYYMMDD = new SimpleDateFormat("YYYYMMdd");
	public static SimpleDateFormat FORMAT_DATE_YYYYMM = new SimpleDateFormat("YYYYMM");
	public static SimpleDateFormat FORMAT_DATE_ISO8601;
	static 	{
		FORMAT_DATE_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		FORMAT_DATE_ISO8601.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	// 0001.01.01 12:00:00 AM +0000
	public static final Date BEGINNING_OF_TIME;

	// new Date(Long.MAX_VALUE) in UTC time zone
	public static final Date END_OF_TIME;
	public static final GregorianCalendar END_OF_TIME_CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

	static {
		END_OF_TIME_CALENDAR.set(1, 0, 1, 0, 0, 0);
		END_OF_TIME_CALENDAR.set(Calendar.MILLISECOND, 0);
		BEGINNING_OF_TIME = END_OF_TIME_CALENDAR.getTime();
		END_OF_TIME = new Date(Long.MAX_VALUE);
		END_OF_TIME_CALENDAR.setTime(END_OF_TIME);
	}

	public static Calendar today(TimeZone tz) {
		Calendar result = Calendar.getInstance(tz == null ? TimeZone.getDefault() : tz);
		int year = result.get(Calendar.YEAR);
		int month = result.get(Calendar.MONTH);
		int date = result.get(Calendar.DATE);
		result.clear();
		result.set(year, month, date);
		return result;
	}

	private boolean showFullDate;
	private TimeZone timezone = null;
	private SimpleDateFormat pattern;

	/**
	 * Construct a new date formatter with null timezone.
	 */
	@Deprecated //does not use timezone!
	public DateFormatter() {
		this(false);
	}

	public DateFormatter(boolean formatFullDate) {
		this(formatFullDate, null);
	}

	public DateFormatter(TimeZone timezone) {
		this(false, timezone);
	}

	public DateFormatter(boolean formatFullDate, TimeZone timezone) {
		this(formatFullDate, timezone, null);
	}
	/**
	 * Construct a new date formatter with null timezone.
	 */
	public DateFormatter(boolean formatFullDate, TimeZone timezone, SimpleDateFormat pattern) {
		super();
		setValueClass(Date.class);
		this.showFullDate = formatFullDate;
		setOverwriteMode(false);
		this.timezone = timezone;
		this.pattern = pattern;
	}

	/**
	 * Take a user entered string and parse it into a date. This is just a wrapper for parseDateToCal.
	 *
	 * @param stringValue
	 * @return date
	 */
	@Override
	public Object stringToValue(String stringValue) {
		Calendar gc = parseDateToCal(stringValue, timezone);
		return gc == null ? null : gc.getTime();
	}

	/**
	 * Takes a date and formats it as a String. This is just a wrapper for formatDate().
	 *
	 * @param value
	 * @return a formatted string if vale is an instance of Date
	 */
	@Override
	public String valueToString(Object value) {
		if (value instanceof Calendar) {
			return formatDate(((Calendar) value).getTime(), this.showFullDate, ((Calendar) value).getTimeZone(), pattern);
		}
		return value instanceof Date ? formatDate((Date) value, this.showFullDate, timezone, pattern) : null;
	}

	/**
	 * Takes a date and formats it as a String. Null dates are returned as an empty string.
	 *
	 * @param dateToFormat
	 * @param showFullAbsoluteValue whether the shortened version is to be used
	 * @return a formatted date string
	 */
	@Deprecated //possibly shold be deprecated, since this method does not take timezone into account.
	public static String formatDate(Date dateToFormat, boolean showFullAbsoluteValue) {
		return formatDate(dateToFormat, showFullAbsoluteValue, TimeZone.getDefault());
	}

	/**
	 * Takes a date and formats it as a String. Null dates are returned as an empty string.
	 *
	 * @param dateToFormat
	 * @return a formatted date string
	 */
	public static String formatDate(Date dateToFormat) {
		return formatDate(dateToFormat, false, TimeZone.getDefault());
	}

	public static String formatDate(Date dateToFormat, boolean showFullAbsoluteValue, TimeZone tz) {
		return formatDate(dateToFormat, showFullAbsoluteValue, tz, null);
	}

	public static String formatDate(Date date, SimpleDateFormat pattern) {
		return formatDate(date, null, pattern);
	}

	public static String formatDate(Date date, TimeZone tz, SimpleDateFormat pattern) {
		return formatDate(date, false, tz, pattern);
	}

	public static String formatDateISO8601(Date date) {
		return formatDate(date, true,TimeZone.getTimeZone("UTC"),  FORMAT_DATE_ISO8601);
	}

	/**
	 * Takes a date and formats it as a String. Null dates are returned as an empty string.
	 *
	 * @param dateToFormat
	 * @param showFullAbsoluteValue whether the shortened version is to be used
	 * @param tz                    tiemzone to format the output in
	 * @return a formatted date string
	 */
	public static String formatDate(Date dateToFormat, boolean showFullAbsoluteValue, TimeZone tz, SimpleDateFormat pattern) {
		if (dateToFormat == null) {
			return "";
		}

		TimeZone timeZone = tz == null ? TimeZone.getDefault() : tz;

		if (!showFullAbsoluteValue) {
			// TODO this is prone to timezone issues
			long msSinceMidnight = dateToFormat.getTime() - today(timeZone).getTimeInMillis();
			if (msSinceMidnight < ONE_DAY_MILLISECONDS && msSinceMidnight >= 0) {
				return "today";
			}
			if (msSinceMidnight < 2 * ONE_DAY_MILLISECONDS && msSinceMidnight >= ONE_DAY_MILLISECONDS) {
				return "tomorrow";
			}
			if (msSinceMidnight < 0 && msSinceMidnight >= -ONE_DAY_MILLISECONDS) {
				return "yesterday";
			}
		}

		Calendar calToFormat = Calendar.getInstance();
		calToFormat.setTime(dateToFormat);

		// show year if not this year
		int year = calToFormat.get(Calendar.YEAR);
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);

		SimpleDateFormat format = pattern == null ? (year == currentYear && !showFullAbsoluteValue) ?
						FORMAT_CURRENT_YEAR : FORMAT_DEFAULT : pattern;

		format.setTimeZone(timeZone);

		return format.format(dateToFormat);
	}



	/**
	 * @param stringValue
	 * @return null for an empty string or a date parsed from the given string
	 */
	public static Date parseDate(String stringValue, TimeZone tz) {
		if (stringValue == null || stringValue.isEmpty()) {
			return null;
		}

		return parseDateToCal(stringValue, tz).getTime();
	}

	/**
	 * Take a user entered string and parse it into a date. Recognised options include
	 * * 2/4/07
	 * * 2/4/2007
	 * * 2 April 2007
	 * * 2-4-07
	 * * 2.4.07
	 * * 020407
	 * * 02042007
	 *
	 * Also allowed are modifiers such as "+2w", "-3y", "+3d" or "+3m". These adjust the value of whatever
	 * date was entered before them. Special options
	 * include:
	 *
	 * * now, today, td, 0 -> today's date
	 * * tomorrow, tm -> today + 1
	 * * yesterday, yd -> today - 1
	 *
	 * The pivot year is calculated as 95 years before today. So if this year is 2007, then 12 -> 2012, but 13 -> 1913.
	 *
	 * @param inputString
	 * @return a Calendar as parsed from the given string
	 */
	public static Calendar parseDateToCal(String inputString, TimeZone tz) {
		inputString = inputString.trim().toLowerCase();
		if (inputString == null || inputString.isEmpty()) {
			return null;
		}

		// add white spaced if necessary, e.g. for "12jan09" -> "12 jan 09"
		char[] ca = inputString.toCharArray();
		boolean allowModify = true;
		boolean isDigit = Character.isDigit(inputString.charAt(0));
		boolean isLetter = Character.isLetter(inputString.charAt(0));
		StringBuilder s = new StringBuilder();
		for (char c : ca) {
			if (c == '+' || c == '-') {
				allowModify = false;
			}
			if (allowModify && isDigit && !isLetter && Character.isLetter(c)) {
				s.append(" ").append(c);
			} else if (allowModify && !isDigit && isLetter && Character.isDigit(c)) {
				s.append(" ").append(c);
			} else {
				s.append(c);
			}
			isDigit = Character.isDigit(c);
			isLetter = Character.isLetter(c);
		}
		inputString = s.toString();

		// reset all the values and leave only the Y/M/D
		Calendar resultDate = today(tz);

		if (inputString.startsWith("-") || inputString.startsWith("+")) {
			parseElement(resultDate, inputString, "", tz);
			return resultDate;
		}
		String[] elements = DATE_PARSE_PTN.split(inputString, 0);
		logger.debug("date parsing split count: {}", elements.length);

		String hint = "day";
		for (String element : elements) {
			hint = parseElement(resultDate, element, hint, tz);
		}

		if (tz != null) {
			resultDate.setTimeZone(tz);
		}
		return resultDate;
	}

	/**
	 * Parse a single piece of the string and update the date with what was parsed.
	 *
	 * @param resultDate
	 * @param inputString
	 * @param hint
	 * @return next hint. Which hint we might reasonably expect to follow this one.
	 */
	protected static String parseElement(Calendar resultDate, String inputString, String hint, TimeZone tz) {
		if (inputString == null) {
			return "";
		}

		/*
		 * adjusts the given calendar according to the stringValue provided. e.g., +25w will add 25 weeks to the calendar, -25d will subtract 25 days, -2y will
		 * subtract two years, 4m will add four months Note: the 'd' character is optional. If no letter other than 'd' is provided then 'd' is assumed.
		 */
		if (inputString.startsWith("+") || inputString.startsWith("-")) {
			try {
				int valueToAdd = Integer.parseInt(numericString(inputString));
				switch (indicator(inputString)) {
					case 'y':
						resultDate.add(Calendar.YEAR, valueToAdd);
						return "";
					case 'm':
						resultDate.add(Calendar.MONTH, valueToAdd);
						return "";
					case 'w':
						resultDate.add(Calendar.DATE, valueToAdd * 7);
						return "";
					default:
						resultDate.add(Calendar.DATE, valueToAdd);
						return "";
				}
			} catch (Exception e) {
				return ""; // do nothing
			}
		}

		// this next bit deals with dates in the format 041106 or 04112006 or 12 (-> 12th of this month)
		try {
			long dateNum = Long.parseLong(inputString);
			if (dateNum > 1011900L) { // 01/01/1900
				resultDate.set(Calendar.YEAR, (int) (dateNum % 10000L));
				resultDate.set(Calendar.MONTH, (int) (dateNum / 10000L % 100L) - 1);
				resultDate.set(Calendar.DATE, (int) (dateNum / 1000000L % 100L));
				return "";
			}

			if (dateNum > 10100L) { // 01/01/00
				resultDate.set(Calendar.YEAR, adjustYear((int) (dateNum % 100L)));
				resultDate.set(Calendar.MONTH, (int) (dateNum / 100L % 100L) - 1);
				resultDate.set(Calendar.DATE, (int) (dateNum / 10000L % 100L));
				return "";
			}

			if (dateNum > 31 || hint.equals("year")) {
				resultDate.set(Calendar.YEAR, adjustYear((int) dateNum));
				return "";
			}

			if (dateNum > 12 || hint.equals("day")) {
				resultDate.set(Calendar.DATE, (int) dateNum);
				return "month";
			}

			resultDate.set(Calendar.MONTH, (int) dateNum - 1); // months start at 0
			return "year";

		} catch (NumberFormatException e) {
			// if it isn't a number, just continue
		}

		if (inputString.equalsIgnoreCase("now") || inputString.equalsIgnoreCase("today") || inputString.equalsIgnoreCase("td")) {
			Calendar today = today(tz);
			resultDate.set(Calendar.YEAR, today.get(Calendar.YEAR));
			resultDate.set(Calendar.MONTH, today.get(Calendar.MONTH));
			resultDate.set(Calendar.DATE, today.get(Calendar.DATE));
			return "";
		}

		if (inputString.equalsIgnoreCase("tomorrow") || inputString.equalsIgnoreCase("tm")) {
			Calendar today = today(tz);
			resultDate.set(Calendar.YEAR, today.get(Calendar.YEAR));
			resultDate.set(Calendar.MONTH, today.get(Calendar.MONTH));
			resultDate.set(Calendar.DATE, today.get(Calendar.DATE) + 1);
			return "";
		}

		if (inputString.equalsIgnoreCase("yesterday") || inputString.equalsIgnoreCase("yd")) {
			Calendar today = today(tz);
			resultDate.set(Calendar.YEAR, today.get(Calendar.YEAR));
			resultDate.set(Calendar.MONTH, today.get(Calendar.MONTH));
			resultDate.set(Calendar.DATE, today.get(Calendar.DATE) - 1);
			return "";
		}

		int index = indexOfCaseInsensitive(longMonths, inputString);
		if (index != -1) {
			resultDate.set(Calendar.MONTH, index);
			return "year";
		}

		index = indexOfCaseInsensitive(shortMonths, inputString);
		if (index != -1) {
			resultDate.set(Calendar.MONTH, index);
			return "year";
		}

		index = indexOfCaseInsensitive(longWeekdays, inputString);
		if (index != -1) {
			resultDate.set(Calendar.DAY_OF_WEEK, index);
			return "day";
		}

		index = indexOfCaseInsensitive(shortWeekdays, inputString);
		if (index != -1) {
			resultDate.set(Calendar.DAY_OF_WEEK, index);
			return "day";
		}

		return "";
	}

	/**
	 * adjust the year for the pivot year
	 */
	private static int adjustYear(int year) {
		if (year >= 100) {
			return year;
		}
		if (year < 20) {
			return year + 2000;
		}
		return year + 1900;
	}

	/**
	 * @return zero or some other number as a string
	 */
	private static String numericString(String stringValue) {
		String in = stringValue == null ? "" : stringValue.trim();
		in = in.replaceAll("[^0-9\\-]", "");
		if (in.length() == 0) {
			return "0";
		}
		return in;
	}

	/**
	 * @return 'd' by default or 'w', 'y', 'm' depending on the end of the value provided
	 */
	private static char indicator(String value) {
		if (value == null || value.length() == 0) {
			return 'd';
		}

		value = value.trim();
		char character = value.length() == 0 ? 'd' : value.charAt(value.length() - 1);

		switch (character) {
			case 'w':
			case 'm':
			case 'y':
				return character;
			default:
				return 'd';
		}
	}

	public static String getDayOfWeekString(Date date, boolean pShort) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		int day = gc.get(GregorianCalendar.DAY_OF_WEEK);
		if (day == GregorianCalendar.MONDAY) {
			return pShort ? "Mon" : "Monday";
		} else if (day == GregorianCalendar.TUESDAY) {
			return pShort ? "Tue" : "Tuesday";
		} else if (day == GregorianCalendar.WEDNESDAY) {
			return pShort ? "Wed" : "Wednesday";
		} else if (day == GregorianCalendar.THURSDAY) {
			return pShort ? "Thu" : "Thursday";
		} else if (day == GregorianCalendar.FRIDAY) {
			return pShort ? "Fri" : "Friday";
		} else if (day == GregorianCalendar.SATURDAY) {
			return pShort ? "Sat" : "Saturday";
		} else if (day == GregorianCalendar.SUNDAY) {
			return pShort ? "Sun" : "Sunday";
		}
		return "";
	}

	private static int indexOfCaseInsensitive(List<String> theList, String value) {
		if (value == null) {
			for (int i = 0; i < theList.size(); i++) {
				if (theList.get(i) == null) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < theList.size(); i++) {
				if (value.equalsIgnoreCase(theList.get(i))) {
					return i;
				}
			}
		}
		return -1;
	}

	public static Date getDatePlusDays(Date input, int shift, boolean setToMidnight) {
		GregorianCalendar temp = new GregorianCalendar();
		temp.setTime(input);

		temp.add(Calendar.DATE, shift);
		if (setToMidnight) {
			temp.set(Calendar.MILLISECOND, 1);
			temp.set(Calendar.SECOND, 0);
			temp.set(Calendar.MINUTE, 0);
			temp.set(Calendar.HOUR, 0);
			temp.set(Calendar.AM_PM, Calendar.AM);
		}
		return temp.getTime();
	}

	public static int subtractDays(Date date1, Date date2) {
		GregorianCalendar gc1 = new GregorianCalendar();
		gc1.setTime(date1);
		GregorianCalendar gc2 = new GregorianCalendar();
		gc2.setTime(date2);

		int days1 = 0;
		int days2 = 0;
		int maxYear = Math.max(gc1.get(Calendar.YEAR), gc2.get(Calendar.YEAR));

		GregorianCalendar gctmp = (GregorianCalendar) gc1.clone();
		for (int f = gctmp.get(Calendar.YEAR); f < maxYear; f++) {
			days1 += gctmp.getActualMaximum(Calendar.DAY_OF_YEAR);
			gctmp.add(Calendar.YEAR, 1);
		}

		gctmp = (GregorianCalendar) gc2.clone();
		for (int f = gctmp.get(Calendar.YEAR); f < maxYear; f++) {
			days2 += gctmp.getActualMaximum(Calendar.DAY_OF_YEAR);
			gctmp.add(Calendar.YEAR, 1);
		}

		days1 += gc1.get(Calendar.DAY_OF_YEAR) - 1;
		days2 += gc2.get(Calendar.DAY_OF_YEAR) - 1;

		return days1 - days2;
	}


	/**
	 * Returns new date object with time value changed to 12.00pm.
	 *
	 * @param input - date
	 */
	public static Date formatDateToNoon(Date input) {
		return DateUtils.setHours(DateUtils.truncate(input, Calendar.HOUR), 12);
	}

	public static String getNextDayOfWeekForToday(Integer neededDayOfWeek, String formatPattern) {
		return getNextDayOfWeek(today(null).getTime(), neededDayOfWeek, formatPattern);
	}

	public static String getNextDayOfWeek(Date start, Integer neededDayOfWeek, String formatPattern) {
		neededDayOfWeek = neededDayOfWeek != null ? neededDayOfWeek : Calendar.SUNDAY;
		SimpleDateFormat format = formatPattern != null ? new SimpleDateFormat(formatPattern) : FORMAT_DATE_DD_MMM_YYYY;
		Calendar date = Calendar.getInstance();
		date.setTime(start);
		return formatDate(getNextDayOfWeek(date, neededDayOfWeek), format);
	}


	public static Date getNextDayOfWeek(Calendar start, int neededDayOfWeek) {

		int dow = start.get(Calendar.DAY_OF_WEEK);

		while (dow != neededDayOfWeek) {
			int date = start.get(Calendar.DATE);
			int month = start.get(Calendar.MONTH);
			int year = start.get(Calendar.YEAR);

			if (date == getMonthLastDate(month, year)) {
				if (month == Calendar.DECEMBER) {
					month = Calendar.JANUARY;
					start.set(Calendar.YEAR, year + 1);
				} else {
					month++;
				}
				start.set(Calendar.MONTH, month);
				date = 1;
			} else {
				date++;
			}

			start.set(Calendar.DATE, date);

			dow = start.get(Calendar.DAY_OF_WEEK);
		}

		return start.getTime();
	}


	private static int getMonthLastDate(int month, int year) {
		switch (month) {
			case Calendar.JANUARY:
			case Calendar.MARCH:
			case Calendar.MAY:
			case Calendar.JULY:
			case Calendar.AUGUST:
			case Calendar.OCTOBER:
			case Calendar.DECEMBER:
				return 31;

			case Calendar.APRIL:
			case Calendar.JUNE:
			case Calendar.SEPTEMBER:
			case Calendar.NOVEMBER:
				return 30;

			default: // Calendar.FEBRUARY
				return year % 4 == 0 ? 29 : 28;
		}
	}

}
