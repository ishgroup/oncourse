package ish.oncourse.portal.components;

import java.util.ArrayList;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.portal.access.IAuthenticationService;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

public class ClassTabs {

	@Parameter
	@Property
	private CourseClass courseClass;

	@Parameter
	private String selected;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Inject
	@Property
	private IAuthenticationService authService;

	@AfterRender
	void afterRender() {
		javaScriptSupport.addScript("jQuery('#%s').addClass('act');", selected);
	}

	public String getClassInfoPageName() {
		return authService.isTutor() ? "tutor/class" : "student/class";
	}

	public String getClassDetailsPageName() {
		return authService.isTutor() ? "tutor/classdetails" : "student/classdetails";
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
