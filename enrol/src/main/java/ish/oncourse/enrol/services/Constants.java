package ish.oncourse.enrol.services;

import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Constants {
	/**
	 * the format is used to parse string value which an user puts in date field.
	 * It uses "yy" format for year because the the format parses years like 11, 73, 85 correctly. For example:
	 * if an user enters 1/1/73 it means 01/01/1973 but not 01/01/0073 which it would be got when it uses format yyyy
	 */
	public static final String DATE_FIELD_PARSE_FORMAT = "dd/MM/yy";

	public static final String DATE_FIELD_SHOW_FORMAT = "dd/MM/yyyy";

	public static final Date MIN_DATE_OF_BIRTH;

	static{
		try {
			MIN_DATE_OF_BIRTH = DateUtils.truncate(new SimpleDateFormat(DATE_FIELD_PARSE_FORMAT).parse("01/01/1900"),
					Calendar.YEAR);
		} catch (ParseException e) {
			throw new IllegalStateException(e);
		}
	}


	/**
	 * Used components names.
	 */
	public static final String COMPONENT_submitContact = "submitContact";
	public static final String COMPONENT_paymentSubmit = "paymentSubmit";

	/**
	 * Event names
	 */
	public static final String EVENT_changePayerEvent = "changePayerEvent";
}
