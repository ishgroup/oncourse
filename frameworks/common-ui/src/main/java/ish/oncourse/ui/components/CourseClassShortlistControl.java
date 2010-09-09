package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Student;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseClassShortlistControl {
	@Parameter
	@Property
	private CourseClass courseClass;
	
	@Inject 
	private IWebSiteService webSiteService;

	public boolean isHasSecureParentPage() {
		return false;
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
		return webSiteService.getCurrentCollege().getIsWebSitePaymentsEnabled();
	}
	
	public boolean isContainedInShortList(){
		//TODO <wo:ISHKeyValueConditional 
		//key="myCookies.shortlist.ids" key1="$object.id.toString" value="$object.id.toString">
		return false;
	}
}
