package ish.oncourse.portal.pages;


import ish.oncourse.model.CourseClass;

import ish.oncourse.model.Outcome;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;
import java.util.TimeZone;

public class Results {
	private static final String KEY_selfPacedMessage = "selfPacedMessage";
	private static final String KEY_classNotHaveSessionsMessage = "classNotHaveSessionsMessage";

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private IPortalService portalService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICourseClassService courseClassService;

    @Property
    private CourseClass courseClass;

    @Property
    private List<CourseClass> courseClasses;

	@Inject
	private Messages messages;

	@SetupRender
    void  setupRender(){

        courseClasses = portalService.getContactCourseClasses(CourseClassFilter.ALL);
    }

    public String getDate()
    {
        if(courseClass.getIsDistantLearningCourse())
            return  messages.get(KEY_selfPacedMessage);

		if(courseClass.getSessions().isEmpty())
			return messages.get(KEY_classNotHaveSessionsMessage);

        TimeZone timeZone = courseClassService.getClientTimeZone(courseClass);

        return String.format("%s - %s",
                FormatUtils.getDateFormat(PortalUtils.DATE_FORMAT_dd_MMMMM_yyyy, timeZone).format(courseClass.getStartDate()),
                FormatUtils.getDateFormat(PortalUtils.DATE_FORMAT_dd_MMMMM_yyyy, timeZone).format(courseClass.getEndDate()));
    }

    public boolean hasResult() {
		return portalService.hasResult(courseClass);
    }


	public String getUrl() {
		return portalService.getUrlBy(courseClass);
	}
}
