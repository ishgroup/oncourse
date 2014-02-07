package ish.oncourse.portal.components;


import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.components.subscriptions.WaitingLists;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import java.util.List;

public class Menu {


    @Parameter
    private String activeMenu;

	@Inject
	private IAuthenticationService authenticationService;

	@Inject
	@Property
	private Request request;

	@Inject
	private IPortalService portalService;

	@Property
	private List<CourseClass> courseClasses;

	@Property
	private CourseClass courseClass;

	@Property
	private List<CourseClass> pastCourseClasses;

	@Property
	private CourseClass nearestCourseClass;



	@SetupRender
	public void setupRender() {
		courseClasses = portalService.getContactCourseClasses(authenticationService.getUser(), CourseClassFilter.CURRENT);

		pastCourseClasses = portalService.getContactCourseClasses(authenticationService.getUser(), CourseClassFilter.PAST);
		nearestCourseClass = !courseClasses.isEmpty() ? courseClasses.get(0) : null;
		if (nearestCourseClass == null)
			nearestCourseClass = !pastCourseClasses.isEmpty() ? pastCourseClasses.get(0) : null;
	}

	public boolean isTutor() {
		return authenticationService.isTutor();
	}

	public String getTimetablePageName() {
		return "timetable";
	}

	public String getDiscussionsPageName() {
		return isTutor() ? "tutor/messages" : "student/messages";
	}

	public String getClassesPageName() {
		return "classes";
	}

	public String getProfilePageName() {
		return "profile";
	}

	public String getMailinglistsPageName() {
		return "mailinglists";
	}

	public String getSurveysPageName() {
		return isTutor() ? "tutor/surveys" : "student/surveys";
	}

	public String getUnconfirmed() {
		return CourseClassFilter.UNCONFIRMED.name();
	}

	public String getCurrent() {
		return CourseClassFilter.CURRENT.name();
	}

	public String getPast() {
		return CourseClassFilter.PAST.name();
	}

	public String getWaitingListsPageName() {
		return WaitingLists.class.getSimpleName();
	}

	public String getContextPath() {
		return request.getContextPath();
	}


    public boolean isHasResources(){

		for (CourseClass courseClass : courseClasses) {
			if (!portalService.getAttachedFiles(courseClass, authenticationService.getUser()).isEmpty()) {
				return true;
			}
		}

		if (authenticationService.isTutor()) {
			return !portalService.getCommonTutorsBinaryInfo().isEmpty();
		}

		return false;
    }


    public boolean isHasResults(){

		if (authenticationService.getUser().getStudent() != null){
			authenticationService.getUser().getStudent().getEnrolments();
			return !authenticationService.getUser().getStudent().getEnrolments().isEmpty();
		}
        return false;
    }

    public boolean isHistoryEnabled()
    {
        return portalService.isHistoryEnabled();
    }


    public String getActiveClassBy(String menutItem)
    {
        if(menutItem.equals(activeMenu))
            return "active";
        return StringUtils.EMPTY;

    }

}
