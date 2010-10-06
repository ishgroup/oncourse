package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Student;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Based on CourseClassCookieView
 * 
 * @author ksenia
 * 
 */
public class CourseClassShortList {

	@Inject 
	private IWebSiteService webSiteService;
	
	@Property
	@Parameter
	private List<CourseClass> orderedClasses;

	@Property
	private CourseClass courseClass;

	public boolean isHasObjects() {
		return orderedClasses != null && !orderedClasses.isEmpty();
	}
	
	public boolean isShowEnrolNow() {
		if(!isPaymentGatewayEnabled()){
			return false;
		}
		return !isEnrolled();
	}
	/**
	 * @return true if the student is already enrolled in this class
	 */
	public boolean isEnrolled() {
		// TODO get current user
		// Contact contact = myApplication().contactForRequest(
		// context().request() );
		Student student = null;// contact == null ? null : contact.student();
		if (student == null) {
			return false;
		}

		for (Enrolment enrolment : student.getActiveEnrolments()) {
			if (enrolment.getCourseClass().equals(courseClass)) {
				return true;
			}
		}
		return false;
	}

	public boolean isPaymentGatewayEnabled() {
		//commented to show the view of enrollment link, because the current value in db is false
		return true;//webSiteService.getCurrentCollege().getIsWebSitePaymentsEnabled();
	}
	public String getEnrolLinkText() {
		return "Enrol in "
				+ (orderedClasses.size() > 1 ? "these classes" : "this class");
	}
}
