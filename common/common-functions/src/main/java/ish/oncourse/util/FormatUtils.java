package ish.oncourse.util;

import ish.math.Money;
import ish.oncourse.model.Session;
import org.apache.commons.lang3.StringUtils;

import java.text.*;
import java.util.Date;
import java.util.TimeZone;

public class FormatUtils {
	public static final String EMPTY_STRING = "";

	public final static DecimalFormat hoursFormat = new DecimalFormat("0.#");

    public final static String dateFormatString = "EEE dd MMM yyyy";

    public final static String shortDateFormatString = "d MMM yyyy";

    public final static String timeFormatString = "hh:mma";

    public final static String shortTimeFormatString = "h:mma";

	public final static String timeFormatWithTimeZoneString = "h:mma ('UTC'Z)";

	public final static String dateFormatForTimeline = "d MMM h:mma";

    public final static String timestampFormat = "d MMM yy h:mma z";


    /**
     * Date/time formats for portal
     */
    public static final String DATE_FORMAT_dd_MMM = "dd MMM";
    public final static String DATE_FORMAT_EEE_dd_MMM = "EEE dd MMM";

    public static final String DATE_FORMAT_MMM_dd = "MMM. dd ";

    public static final String DATE_FORMAT_dd_MMM_E = "dd/MMM/E";

    public static final String DATE_FORMAT_dd_MMM_E_yyyy = "dd/MMM/E/yyyy";

    public static final String DATE_FORMAT_MM_yyyy = "MM-yyyy";

    public static final String DATE_FORMAT_MMM = "MMM";
    public static final String TIME_FORMAT_h_mm_a = "h:mm a";

    public static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final String TIME_ZONE_UTC = "UTC";

    private static Format feeFormatWithCents;

    private static Format feeFormatWithoutCents;

    /**
     * the format is used to parse string value which an user puts in date field.
     * It uses "yy" format for year because the the format parses years like 11, 73, 85 correctly. For example:
     * if an user enters 1/1/73 it means 01/01/1973 but not 01/01/0073 which it would be got when it uses format yyyy
     */
    public static final String DATE_FIELD_PARSE_FORMAT = "dd/MM/yy";

    public static final String DATE_FIELD_SHOW_FORMAT = "dd/MM/yyyy";


    public static DateFormat getDateFormat_dd_MMM_E_yyyy(String timeZone) {
        return FormatUtils.getDateFormat(DATE_FORMAT_dd_MMM_E_yyyy, timeZone);
    }

    public static DateFormat getDateFormat_dd_MMM_E(String timeZone) {
        return FormatUtils.getDateFormat(DATE_FORMAT_dd_MMM_E, timeZone);
    }

    public static DateFormat getTimeFormat_h_mm_a(String timeZone) {
        return FormatUtils.getDateFormat(TIME_FORMAT_h_mm_a, timeZone);
    }

    public  static DateFormat getDateFormat(String format, String timeZone) {
        return createDateFormat(format, timeZone);
    }
    
    public  static DateFormat getDateFormat(String timeZone) {
        return createDateFormat(dateFormatString, timeZone);
    }

    public  static DateFormat getShortDateFormat(String timeZone) {
        return createDateFormat(shortDateFormatString, timeZone);
    }

    public  static DateFormat getTimeFormat(String timeZone) {
        return createDateFormat(timeFormatString, timeZone);
    }

    public  static DateFormat getShortTimeFormat(String timeZone) {
        return createDateFormat(shortTimeFormatString, timeZone);
    }

    public  static DateFormat getDateFormatForTimeline(String timeZone) {
        return createDateFormat(dateFormatForTimeline, timeZone);
    }

    public  static DateFormat getTimestampFormat(String timeZone) {
        return createDateFormat(timestampFormat, timeZone);
    }

    public  static DateFormat getFullDateFormat(String timeZone) {
        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL);
        if (timeZone != null) {
            format.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        return format;
    }

    private static DateFormat createDateFormat(String format, String timeZone) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        if (StringUtils.trimToNull(timeZone) != null) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        return dateFormat;
    }

	public static DateFormat getDateFormat(String format, TimeZone timeZone) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(timeZone);
		return dateFormat;
	}

    public static Format getFeeFormatWithCents() {
        if (feeFormatWithCents == null) {
            feeFormatWithCents = MoneyFormat.getInstance(2);
        }
        return feeFormatWithCents;
    }

    public static Format getFeeFormatWithoutCents() {
        if (feeFormatWithoutCents == null) {
            feeFormatWithoutCents = MoneyFormat.getInstance(0);
        }
        return feeFormatWithoutCents;
    }

    public static Format chooseMoneyFormat(Money amount) {
        Format format;
        if (amount.getCents() > 0) {
            format = getFeeFormatWithCents();
        } else {
            format = getFeeFormatWithoutCents();
        }
        return format;
    }

    public static String convertDateToISO8601(Date date) {
    	return convertDateToISO8601InUserTimezone(date, TIME_ZONE_UTC);
    }
    
    public static String convertDateToISO8601InUserTimezone(Date date, String timeZone) {
    	return FormatUtils.getDateFormat(DATE_FORMAT_ISO8601, timeZone).format(date);
    }

    public static String getSessionTimeAsString(Session session) {
        TimeZone timeZone = TimeZone.getDefault();
        if (!StringUtils.isBlank(session.getTimeZone())) {
            timeZone = TimeZone.getTimeZone(session.getTimeZone());
        }
        CustomizedDateFormat dateFormat = new CustomizedDateFormat(FormatUtils.TIME_FORMAT_h_mm_a, timeZone);
        return String.format("%s - %s", dateFormat.format(session.getStartDate()),
                dateFormat.format(session.getEndDate())).toLowerCase();
    }
}
