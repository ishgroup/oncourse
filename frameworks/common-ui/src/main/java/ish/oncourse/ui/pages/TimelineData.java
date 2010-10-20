package ish.oncourse.ui.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.IPageRenderer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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

	@Inject
	private IPageRenderer pageRenderer;

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

	public String getEventContent() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("timelineRecord", record);
		return pageRenderer.encodedPage("ui/TimelineEventDetail", parameters);
	}

	public String getStartDate() {
		DateFormat format = DateFormat.getDateInstance(DateFormat.FULL);
		format.setTimeZone(TimeZone.getTimeZone(record.getTimeZone()));
		return format.format(record.getStartDate());
	}

	public String getEndDate() {
		DateFormat format = DateFormat.getDateInstance(DateFormat.FULL);
		format.setTimeZone(TimeZone.getTimeZone(record.getTimeZone()));
		return format.format(record.getEndDate());
	}

}
