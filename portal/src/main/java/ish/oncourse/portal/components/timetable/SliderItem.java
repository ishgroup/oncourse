package ish.oncourse.portal.components.timetable;

import ish.oncourse.model.Session;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.util.DateFormatter;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.TimeZone;

/**
 * User: artem
 * Date: 10/11/13
 * Time: 3:23 PM
 */
public class SliderItem {

    @Inject
    private ICookiesService cookiesService;

    @Parameter
    private Session session;

    private TimeZone timeZone;

    @SetupRender
    void setupRender() {
        timeZone = FormatUtils.getClientTimeZone(session.getCourseClass());
    }

    public String getPeriod()
    {

        return DateFormatter.formatDate(session.getStartDate(),false,FormatUtils.getClientTimeZone(session.getCourseClass()));
    }

    public String getName()
    {
        return session.getCourseClass().getCourse().getName();
    }

    public String getDate()
    {
        return String.format("%s - %s",
                FormatUtils.getDateFormat(FormatUtils.dateFormatForTimeline, timeZone).format(session.getStartDate()),
                FormatUtils.getDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone).format(session.getEndDate()));
    }

    public Date getEndDate()
    {
        return session.getEndDate();
    }

    public String getVenue()
    {

        if (session.getRoom() != null)
            return String.format("%s, %s", session.getRoom().getName(), session.getRoom().getSite().getName());
        else
            return StringUtils.EMPTY;
    }

}

