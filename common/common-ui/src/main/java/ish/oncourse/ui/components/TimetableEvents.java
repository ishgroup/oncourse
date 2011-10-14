package ish.oncourse.ui.components;

import ish.oncourse.model.Session;
import ish.oncourse.ui.utils.FormatUtils;

import java.text.Format;
import java.util.List;

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
		return FormatUtils.getDateFormat(event.getTimeZone());
	}

	public Format getItemTimeFormatter() {
		return FormatUtils.getTimeFormat(event.getTimeZone());
	}

	public boolean isHasItemEndDate() {
		return event.getEndDate() != null;
	}
}
