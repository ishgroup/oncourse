package ish.oncourse.util;

import ish.math.Country;
import ish.math.Money;

import java.text.*;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

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
    public static final String DATE_FORMAT_MMM_dd = "MMM. dd ";

    public static final String DATE_FORMAT_dd_MMM_E = "dd/MMM/E";

    public static final String DATE_FORMAT_dd_MMM_E_yyyy = "dd/MMM/E/yyyy";

    public static final String DATE_FORMAT_MM_yyyy = "MM-yyyy";

    public static final String DATE_FORMAT_MMM = "MMM";
    public static final String TIME_FORMAT_h_mm_a = "h:mm a";

    public static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'hh:mm:ss'Z'";

    public static final String TIME_ZONE_UTC = "UTC";

    private static NumberFormat feeFormatWithCents;

    private static NumberFormat feeFormatWithoutCents;


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

    public static NumberFormat getFeeFormatWithCents() {
        if (feeFormatWithCents == null) {
            feeFormatWithCents = NumberFormat.getCurrencyInstance(Country.AUSTRALIA.locale());
            feeFormatWithCents.setMinimumFractionDigits(2);
        }
        return feeFormatWithCents;
    }

    public static NumberFormat getFeeFormatWithoutCents() {
        if (feeFormatWithoutCents == null) {
            feeFormatWithoutCents = NumberFormat.getCurrencyInstance(Country.AUSTRALIA.locale());
            ((NumberFormat) feeFormatWithoutCents).setMinimumFractionDigits(0);
        }
        return feeFormatWithoutCents;
    }

    public static Format chooseMoneyFormat(Money amount) {
        NumberFormat format;
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
}
