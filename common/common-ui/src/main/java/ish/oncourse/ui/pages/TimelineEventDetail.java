package ish.oncourse.ui.pages;

import ish.oncourse.model.Session;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.Format;

public class TimelineEventDetail {
	@Inject
	private Request request;

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
