package ish.oncourse.portal.components.timetable;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.util.DateFormatter;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
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

    @Parameter
    private CourseClass courseClass;

    private TimeZone timeZone;

	@Inject
	private Messages messages;

    @SetupRender
    void setupRender() {
        timeZone = FormatUtils.getClientTimeZone(courseClass);
    }

    public String getPeriod()
    {
		if(courseClass.getIsDistantLearningCourse())
			return  messages.get(KEY_selfPacedMessage);

		if(courseClass.getSessions().isEmpty())
			return messages.get(KEY_classNotHaveSessionsMessage);

        return DateFormatter.formatDate(courseClass.getSessions().get(0).getStartDate(),false,FormatUtils.getClientTimeZone(courseClass));
    }

    public String getName()
    {
        return courseClass.getCourse().getName();
    }

    public String getDate()
    {
		if(courseClass.getIsDistantLearningCourse())
			return  messages.get(KEY_selfPacedMessage);

		if(courseClass.getSessions().isEmpty())
			return messages.get(KEY_classNotHaveSessionsMessage);

        return String.format("%s - %s",
                FormatUtils.getDateFormat(FormatUtils.dateFormatForTimeline, timeZone).format(courseClass.getSessions().get(0).getStartDate()),
                FormatUtils.getDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone).format(courseClass.getSessions().get(0).getEndDate()));
    }


    public String getVenue()
    {

        if (courseClass.getRoom() != null)
            return String.format("%s, %s", courseClass.getRoom().getName(), courseClass.getRoom().getSite().getName());
        else
            return StringUtils.EMPTY;
    }

    public CourseClass getCourseClass(){
        return courseClass;
    }
}

