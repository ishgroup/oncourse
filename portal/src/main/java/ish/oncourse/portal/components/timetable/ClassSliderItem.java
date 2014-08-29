package ish.oncourse.portal.components.timetable;

import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PCourseClass;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.DateFormatter;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.TimeZone;

/**
 * User: artem
 * Date: 10/11/13
 * Time: 3:23 PM
 */
public class ClassSliderItem {

	private static final String KEY_selfPacedMessage = "selfPacedMessage";
	private static final String KEY_classNotHaveSessionsMessage = "classNotHaveSessionsMessage";

    @Inject
    private ICookiesService cookiesService;

	@Inject
	private ICourseClassService courseClassService;

    @Parameter
    @Property
	private PCourseClass pCourseClass;

    private TimeZone timeZone;

	@Inject
	private Messages messages;

    @Inject
    @Property
    private Request request;

    @Inject
    private IPortalService portalService;

    @SetupRender
    void setupRender() {
        timeZone = courseClassService.getClientTimeZone(pCourseClass.getCourseClass());
    }

    public String getPeriod()
    {
		if(pCourseClass.getCourseClass().getIsDistantLearningCourse())
			return  messages.get(KEY_selfPacedMessage);

		if(pCourseClass.getCourseClass().getSessions().isEmpty())
			return messages.get(KEY_classNotHaveSessionsMessage);

        return DateFormatter.formatDate(pCourseClass.getStartDate(), false, courseClassService.getClientTimeZone(pCourseClass.getCourseClass()));
    }

    public String getName()
    {
        return pCourseClass.getCourseClass().getCourse().getName();
    }

    public String getDate()
    {
		if(pCourseClass.getCourseClass().getIsDistantLearningCourse())
			return  messages.get(KEY_selfPacedMessage);

		if(pCourseClass.getCourseClass().getSessions().isEmpty())
			return messages.get(KEY_classNotHaveSessionsMessage);

        return String.format("%s - %s",
				FormatUtils.getDateFormat(FormatUtils.dateFormatForTimeline, timeZone).format(pCourseClass.getStartDate()),
				FormatUtils.getDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone).format(pCourseClass.getEndDate()));
    }


    public String getVenue()
    {

        if (pCourseClass.getCourseClass().getRoom() != null)
            return String.format("%s, %s", pCourseClass.getCourseClass().getRoom().getName(), pCourseClass.getCourseClass().getRoom().getSite().getName());
        else
            return StringUtils.EMPTY;
    }

    public CourseClass getCourseClass(){
        return pCourseClass.getCourseClass();
    }

	public Object[] getAccountContext()
	{
		return new Object[] { pCourseClass.getCourseClass().getId(), "location" };
	}

    public boolean needApprove() {
        return !portalService.isApproved(pCourseClass.getCourseClass());
    }
}

