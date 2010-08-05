package ish.oncourse.website.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Student;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class CourseClassShortlistControl {
	@Parameter
	@Property
	private CourseClass courseClass;

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

	public boolean isMyCollegePaymentGatewayEnabled() {
		// TODO $myCollege.PaymentGatewayEnabled
		return true;
	}
}
