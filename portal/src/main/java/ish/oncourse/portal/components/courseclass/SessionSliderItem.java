package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.Session;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 11:16 AM
 */
public class SessionSliderItem {

    private TimeZone timeZone;

	@Property
	@Parameter
	private boolean isTutor;

    @Property
    @Parameter
    private Session nearestSession;

    @Property
    @Parameter
    private Session session;

    @Property
    private List<Tutor> tutors;

    @Property
    private Tutor tutor;

    @Inject
    private IAuthenticationService authenticationService;


    @SetupRender
    boolean setupRender() {
        timeZone = session.getCourseClass().getClassTimeZone();

        tutors = new ArrayList<>();
        List<TutorRole> tutorRoles = session.getCourseClass().getTutorRoles();
        for (TutorRole tutorRole : tutorRoles) {
            tutors.add(tutorRole.getTutor());
        }


        return true;
    }

    public String getDay(){

        return  FormatUtils.getDateFormat("dd",timeZone).format(session.getStartDate());
    }

    public String getMonth(){

        return  FormatUtils.getDateFormat("MMM",timeZone).format(session.getStartDate());
    }

    public String getDate()
    {
        return String.format("%s - %s",
                FormatUtils.getDateFormat("EEEE h:mma", timeZone).format(session.getStartDate()),
                FormatUtils.getDateFormat("h:mma ('UTC'Z)", timeZone).format(session.getEndDate()));
    }
    public String getVenue()
    {

        if (session.getCourseClass().getRoom() != null)
            return String.format("%s, %s", session.getCourseClass().getRoom().getName(), session.getCourseClass().getRoom().getSite().getName());
        else
            return StringUtils.EMPTY;
    }

    public String getCurrentClass(){

        return session.getId().equals(nearestSession.getId()) ? "current" : StringUtils.EMPTY;
    }


}
