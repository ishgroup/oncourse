package ish.oncourse.ui.pages;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.services.courseclass.ICourseClassService;

import org.apache.tapestry5.annotations.Meta;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

@Meta("tapestry.response-content-type=text/xml")
public class TimelineData {

	@Inject
	private ICourseClassService courseClassService;

	@Inject
	private Request request;

	private List<CourseClass> courseClasses;
	@Property
	private List<Session> records;

	@Property
	private Session record;

	private static final String FORMAT_FOR_TITLE = "d MMM h:mma";
	private static final String TIMESTAMP_FORMAT = "d MMM yy h:mma z";

	@SetupRender
	void beforeRender() {
		String ids = request.getParameter("ids");
		if (ids != null) {
			String[] idsStings = ids.split(",");
			List<Long> validIds = new ArrayList<Long>(idsStings.length);
			for (String idStr : idsStings) {
				if (idStr.matches("(\\d+)")) {
					validIds.add(Long.valueOf(idStr));
				}
			}
			courseClasses = courseClassService.loadByIds(validIds.toArray());
			records = new ArrayList<Session>();
			for (CourseClass cc : courseClasses) {
				records.addAll(cc.getTimelineableSessions());
			}
		}
	}

	public String getRecordTitle() {
		String result = null;

		DateFormat formatter = new SimpleDateFormat(FORMAT_FOR_TITLE);

		formatter.setTimeZone(TimeZone.getTimeZone(record.getTimeZone()));

		if (record.getStartDate() != null) {
			result = formatter.format(record.getStartDate());
		} else {
			result = "TBA";
		}
		return result;
	}

	public Format getTimestampFormatter() {
		SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone(record.getTimeZone()));
		return formatter;
	}
}
