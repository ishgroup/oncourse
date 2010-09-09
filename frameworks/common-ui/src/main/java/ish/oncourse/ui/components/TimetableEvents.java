package ish.oncourse.ui.components;

import ish.oncourse.model.Session;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class TimetableEvents {

	@Property
	@Parameter
	private List<Session> displayedObjects;

	@Property
	private Session event;

	@Property
	private Integer listIndex;

	@Property
	@Parameter
	private List<String> headerLabels;

	@Property
	private String label;

	@Property
	@Parameter
	private String cssTableClass;

	@Property
	@Parameter
	private String cssEvenRowClass;

	@Property
	@Parameter
	private String cssOddRowClass;

	@Property
	@Parameter
	private String dateFormat;

	@Property
	@Parameter
	private String timeFormat;

	public String getCssRowClass() {
		if (isOddRow()) {
			return cssOddRowClass;
		} else if (isEvenRow()) {
			return cssEvenRowClass;
		}
		return "";
	}

	public boolean isEvenRow() {
		return this.listIndex != null && this.listIndex.intValue() % 2 == 0;
	}

	public boolean isOddRow() {
		return this.listIndex != null && this.listIndex.intValue() % 2 != 0;
	}

	public Format getItemDateFormatter() {
		DateFormat format = new SimpleDateFormat(dateFormat);
		format.setTimeZone(TimeZone.getTimeZone(event.getTimeZone()));
		return format;
	}

	public Format getItemTimeFormatter() {
		DateFormat format = new SimpleDateFormat(timeFormat);
		format.setTimeZone(TimeZone.getTimeZone(event.getTimeZone()));
		return format;
	}

	public boolean isHasItemEndDate() {
		return event.getEndDate() != null;
	}
}
