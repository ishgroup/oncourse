package ish.oncourse.ui.utils;

import ish.math.Country;
import ish.math.Money;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class FormatUtils {
	public final static DecimalFormat hoursFormat = new DecimalFormat("0.#");

	public final static String dateFormatString = "EEE dd MMM yyyy";

	public final static String shortDateFormatString = "d MMM yyyy";

	public final static String timeFormatString = "hh:mma";

	public final static String shortTimeFormatString = "h:mma";

	public final static String dateFormatForTimeline = "d MMM h:mma";

	public final static String timestampFormat = "d MMM yy h:mma z";

	private static NumberFormat feeFormatWithCents;

	private static NumberFormat feeFormatWithoutCents;

	public final static DateFormat getDateFormat(String timeZone) {
		return createDateFormat(dateFormatString, timeZone);
	}

	public final static DateFormat getShortDateFormat(String timeZone) {
		return createDateFormat(shortDateFormatString, timeZone);
	}

	public final static DateFormat getTimeFormat(String timeZone) {
		return createDateFormat(timeFormatString, timeZone);
	}

	public final static DateFormat getShortTimeFormat(String timeZone) {
		return createDateFormat(shortTimeFormatString, timeZone);
	}

	public final static DateFormat getDateFormatForTimeline(String timeZone) {
		return createDateFormat(dateFormatForTimeline, timeZone);
	}

	public final static DateFormat getTimestampFormat(String timeZone) {
		return createDateFormat(timestampFormat, timeZone);
	}

	public final static DateFormat getFullDateFormat(String timeZone) {
		DateFormat format = DateFormat.getDateInstance(DateFormat.FULL);
		if (timeZone != null) {
			format.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		return format;
	}

	private static DateFormat createDateFormat(String format, String timeZone) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		if (timeZone != null) {
			dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
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
}
