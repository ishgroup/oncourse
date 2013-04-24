package ish.oncourse.util;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

public class CustomizedDateFormat extends SimpleDateFormat {
	private static final long serialVersionUID = -4933085840547588217L;

	public CustomizedDateFormat(String format, TimeZone timeZone) {
		super(format);
		if (timeZone != null) {
			setTimeZone(timeZone);
		}
	}

	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
		String result = super.format(date, toAppendTo, pos).toString().replace(":00", StringUtils.EMPTY).replace("PM", "pm").replace("AM", "am");
		int timeZoneFormatIndex = result.indexOf("(UTC");
		if (timeZoneFormatIndex >-1) {
			//custom format used to show the timezone offset
			String replacedText = result.substring(timeZoneFormatIndex + "(UTC ".length());
			result = result.replace(replacedText, replacedText.subSequence(0, 2) + ":" + replacedText.subSequence(2, 4) + ")");
		}
		StringBuffer formatResult = new StringBuffer(result);
		return formatResult;
	}
	
	

}
