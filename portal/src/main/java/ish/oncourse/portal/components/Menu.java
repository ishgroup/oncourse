package ish.oncourse.portal.components;


import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PCourseClass;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;
import java.util.TimeZone;

import static ish.oncourse.portal.services.PortalUtils.DATE_FORMAT_d_MMMM_h_mma_UTC_Z;

public class Menu {


    @Parameter
    private String activeMenu;

	@Inject
	private IAuthenticationService authenticationService;

	@Inject
	@Property
	private Request request;

	@Inject
	private ICourseClassService courseClassService;

	@Inject
	private IPortalService portalService;

	@Property
	private CourseClass pastCourseClass;

	@Property
	private List<CourseClass> pastCourseClasses;

	@Property
	private CourseClass nearestCourseClass;

	@Property
	private List<PCourseClass> pCourseClasses;

	@Property
	private PCourseClass pCourseClass;


	@SetupRender
	public void setupRender() {

		pCourseClasses = portalService.fillCourseClassSessions(CourseClassFilter.CURRENT);

		pastCourseClasses = portalService.getContactCourseClasses(CourseClassFilter.PAST);

		nearestCourseClass = !pCourseClasses.isEmpty() ? pCourseClasses.get(0).getCourseClass() : null;

		if (nearestCourseClass == null)
			nearestCourseClass = !pastCourseClasses.isEmpty() ? pastCourseClasses.get(0) : null;
	}

	public String getDate(Object courseClass) {
		TimeZone timeZone;

		if (courseClass instanceof PCourseClass) {
			timeZone = courseClassService.getClientTimeZone(pCourseClass.getCourseClass());
			return 	FormatUtils.getDateFormat(DATE_FORMAT_d_MMMM_h_mma_UTC_Z, timeZone).format(pCourseClass.getStartDate());
		}

		if (courseClass instanceof CourseClass) {
			timeZone = courseClassService.getClientTimeZone(pastCourseClass);
			return 	FormatUtils.getDateFormat(DATE_FORMAT_d_MMMM_h_mma_UTC_Z, timeZone).format(pastCourseClass.getStartDate());
		}
		return StringUtils.EMPTY;

	}

    public boolean needApprove() {
        return authenticationService.isTutor() && !portalService.isApproved(pCourseClass.getCourseClass());
    }
}
