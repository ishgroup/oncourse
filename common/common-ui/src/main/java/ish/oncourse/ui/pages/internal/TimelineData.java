package ish.oncourse.ui.pages.internal;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.IPageRenderer;
import org.apache.tapestry5.annotations.Meta;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.util.*;

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

	@Inject
	private IPageRenderer pageRenderer;

	@SetupRender
	void beforeRender() {
		String ids = request.getParameter("ids");
		if (ids != null) {
			String[] idsStings = ids.split(",");
			List<Long> validIds = new ArrayList<>(idsStings.length);
			for (String idStr : idsStings) {
				if (idStr.matches("(\\d+)")) {
					validIds.add(Long.valueOf(idStr));
				}
			}
			courseClasses = courseClassService.loadByIds(validIds);
			records = new ArrayList<>();
			for (CourseClass cc : courseClasses) {
				records.addAll(cc.getTimelineableSessions());
			}
		}
	}

	public String getRecordTitle() {
		String result = null;

		DateFormat formatter = FormatUtils.getDateFormatForTimeline(record.getTimeZone());

		if (record.getStartDate() != null) {
			result = formatter.format(record.getStartDate());
		} else {
			result = "TBA";
		}
		return result;
	}

	public String getEventContent() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("timelineRecord", record);
		return pageRenderer.encodedPage("ui/TimelineEventDetail", parameters);
	}

	public String getStartDate() {
		DateFormat format = FormatUtils.getFullDateFormat(record.getTimeZone());
		return format.format(record.getStartDate());
	}

	public String getEndDate() {
		DateFormat format = FormatUtils.getFullDateFormat(record.getTimeZone());
		return format.format(record.getEndDate());
	}

}
