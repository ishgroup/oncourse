package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.binary.IBinaryDataService;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

public class ClassTabs {

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private IBinaryDataService binaryDataService;

	@Parameter
	@Property
	private CourseClass courseClass;

	//@Parameter
	//private String selected;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Inject
	@Property
	private IAuthenticationService authService;

	@Inject
	private Request request;
	
	public String getContextPath() {
		return request.getContextPath();
	}

    public boolean isClassWithModules(){
        return !courseClass.getCourse().getModules().isEmpty() && !authenticationService.getUser().getStudent().getEnrolments().isEmpty();
    }


    public boolean isHasResources(){

        return  !binaryDataService.getAttachedFiles(courseClass.getId(), CourseClass.class.getSimpleName(), false).isEmpty();
    }

    public String getClassInfoPageName() {
		return "class";
	}

	public String getClassDetailsPageName() {
		return "classdetails";
	}

	public String getClassSurveyPageName()
	{
		return authService.isTutor() ? "tutor/surveys" : "student/surveys";
	}

	public boolean isShowTutorTabs() {
		if(authService.isTutor()) {
			Tutor tutor = authService.getUser().getTutor();
			boolean isInCourseClassOrSessions = false;
			for (TutorRole t: courseClass.getTutorRoles()) {
				isInCourseClassOrSessions = (tutor.getId().equals(t.getTutor().getId()));
				if(isInCourseClassOrSessions){
					break;
				}
			}
			if(!isInCourseClassOrSessions){
				for (Session s: courseClass.getSessions()) {
					for(SessionTutor t: s.getSessionTutors()) {
						isInCourseClassOrSessions = (tutor.getId().equals(t.getTutor().getId()));
						if(isInCourseClassOrSessions){
							break;
						}
					}
				}
			}
			
			return isInCourseClassOrSessions;
		}
		return  false;
	}
}
