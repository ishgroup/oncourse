package ish.oncourse.ui.pages;

import ish.oncourse.model.Session;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class TimelineEventDetail {
	@Inject 
	private Request request;
	@Property
	private Session record;
	private static final String TIMESTAMP_FORMAT = "d MMM yy h:mma z";

	@SetupRender
	void beginRender(){
		record = (Session) request.getAttribute("timelineRecord");
	}
	public Format getTimestampFormatter() {
		SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone(record.getTimeZone()));
		return formatter;
	}
}
