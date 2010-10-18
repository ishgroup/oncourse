package ish.oncourse.ui.components;

import ish.oncourse.utils.TimestampUtilities;

import java.util.Date;
import java.util.TimeZone;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class DateRangeDisplay {

	private static final String doubleDateformat = "EE d MMM";
	private static final String singleDateformat = "EEEE d MMMM";
	private static final String yearformat = "yyyy";
	private static final String machineFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	@Parameter
	private Date date1;

	@Parameter
	private Date date2;

	@Parameter
	private TimeZone timeZone;

	@Property
	private boolean hasFullRange;

	@Property
	private String humanStartDate;

	@Property
	private String humanEndDate;

	@SetupRender
	void beforeRender() {
		setupDisplayedRange();
	}

	public void setupDisplayedRange() {
		String year = TimestampUtilities.userPresentableDate(date1, yearformat,
				timeZone);

		String startDay = TimestampUtilities.userPresentableDate(date1,
				"DDD yyyy", timeZone);
		String finishDay = TimestampUtilities.userPresentableDate(date2,
				"DDD yyyy", timeZone);

		if (startDay.equals(finishDay)) {
			hasFullRange = false;
			humanStartDate = TimestampUtilities.userPresentableDate(date1,
					singleDateformat, timeZone)
					+ " " + year;

		} else {
			hasFullRange = true;
			humanStartDate = TimestampUtilities.userPresentableDate(date1,
					doubleDateformat, timeZone);

			String year2 = TimestampUtilities.userPresentableDate(date2,
					yearformat, timeZone);

			if (!year.equals(year2)) {
				humanStartDate += " " + year;
			}
			humanEndDate = TimestampUtilities.userPresentableDate(date2,
					doubleDateformat, timeZone)
					+ " " + year2;
		}
	}

	public String getMachineStartDate() {
		return TimestampUtilities.userPresentableDate(date1, machineFormat,
				timeZone);
	}

	public String getMachineEndDate() {
		return TimestampUtilities.userPresentableDate(date2, machineFormat,
				timeZone);
	}

}
