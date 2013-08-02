package ish.oncourse.ui.components;

import ish.oncourse.model.Session;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.util.CustomizedDateFormat;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
import java.util.List;
import java.util.TimeZone;

public class TimetableEvents {

	@Inject
	private ICookiesService cookiesService;
	
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
	
	public String getCssRowClass() {
		if (isOddRow()) {
			return cssOddRowClass;
		} else if (isEvenRow()) {
			return cssEvenRowClass;
		}
		return StringUtils.EMPTY;
	}

	public boolean isEvenRow() {
		return this.listIndex != null && this.listIndex % 2 == 0;
	}

	public boolean isOddRow() {
		return this.listIndex != null && this.listIndex % 2 != 0;
	}

	public Format getItemDateFormatter() {
		TimeZone timeZone = getClientTimeZone();
		return FormatUtils.getDateFormat(FormatUtils.dateFormatString, timeZone);
	}

	public Format getItemTimeFormatter() {
		TimeZone timeZone = getClientTimeZone();
		return new CustomizedDateFormat(FormatUtils.shortTimeFormatString, timeZone);
	}
	
	public Format getItemTimeFormatterWithTimeZone() {
		TimeZone timeZone = getClientTimeZone();
		return new CustomizedDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone);
	}

	public boolean isHasItemEndDate() {
		return event.getEndDate() != null;
	}
	
	public TimeZone getClientTimeZone() {
		TimeZone timezone = null;
		if (!event.isVirtualSiteUsed()) {
			timezone = TimeZone.getTimeZone(event.getTimeZone());
		}
		if (timezone == null) {
			timezone = cookiesService.getClientTimezone();
			if (timezone == null) {
				timezone = cookiesService.getSimpleClientTimezone();
				if (timezone == null) {
					timezone = TimeZone.getTimeZone(event.getTimeZone());
				}
			}
		}
		return timezone;
	}
}
