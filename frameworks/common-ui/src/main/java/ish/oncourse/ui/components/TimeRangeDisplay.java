package ish.oncourse.ui.components;

import ish.oncourse.utils.TimestampUtilities;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.tapestry5.annotations.Parameter;

public class TimeRangeDisplay {

	private static final String HOURS_FORMAT = "ha";

	private static final String HOURS_MINUTES_FORMAT = "h:mma";

	@Parameter
	private Date startTime;

	@Parameter
	private Date endTime;

	@Parameter
	private TimeZone timeZone;

	public String getDisplayedRange() {

		StringBuffer buff = new StringBuffer();
		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
		buff.append(TimestampUtilities.userPresentableDate(startTime, start
				.get(Calendar.MINUTE) == 0 ? HOURS_FORMAT
				: HOURS_MINUTES_FORMAT, timeZone));

		buff.append(" &#8211; ").append(
				TimestampUtilities.userPresentableDate(endTime, end
						.get(Calendar.MINUTE) == 0 ? HOURS_FORMAT
						: HOURS_MINUTES_FORMAT, timeZone));

		return buff.toString();
	}
}
