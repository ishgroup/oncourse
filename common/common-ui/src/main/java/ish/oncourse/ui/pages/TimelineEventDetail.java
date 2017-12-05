package ish.oncourse.ui.pages;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.Session;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.text.Format;

public class TimelineEventDetail extends ISHCommon {
	@Property
	private Session record;

	@Property
	private Format timestampFormatter;

	@SetupRender
	void beginRender() {
		record = (Session) request.getAttribute("timelineRecord");
		timestampFormatter = FormatUtils.getTimestampFormat(record.getTimeZone());
	}

}
