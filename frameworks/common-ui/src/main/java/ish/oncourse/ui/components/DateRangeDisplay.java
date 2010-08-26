package ish.oncourse.ui.components;

import ish.oncourse.utils.TimestampUtilities;

import java.util.Date;
import java.util.TimeZone;

import org.apache.tapestry5.annotations.Parameter;

public class DateRangeDisplay {

	private static final String doubleDateformat = "EE d MMM";
	private static final String singleDateformat = "EEEE d MMMM";
	private static final String yearformat = "yyyy";

	@Parameter
	private Date date1;

	@Parameter
	private Date date2;

	@Parameter
	private TimeZone timeZone;

	public String getDisplayedRange() {
		String year = TimestampUtilities.userPresentableDate(date1,
				this.yearformat, timeZone);

		String startDay = TimestampUtilities.userPresentableDate(date1,
				"DDD yyyy", timeZone);
		String finishDay = TimestampUtilities.userPresentableDate(date2,
				"DDD yyyy", timeZone);

		if (startDay.equals(finishDay)) {
			return TimestampUtilities.userPresentableDate(date1,
					this.singleDateformat, timeZone)
					+ " " + year;
		}

		StringBuffer buff = new StringBuffer();
		buff.append(
				TimestampUtilities.userPresentableDate(date1,
						this.doubleDateformat, timeZone)).append(" ");

		String year2 = TimestampUtilities.userPresentableDate(date2,
				this.yearformat, timeZone);

		if (!year.equals(year2)) {
			buff.append(year).append(" ");
		}
		buff.append(" &#8211; ").append(
				TimestampUtilities.userPresentableDate(date2,
						this.doubleDateformat, timeZone));

		buff.append(" ").append(year2);
		return buff.toString();
	}
}
