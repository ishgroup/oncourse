package ish.oncourse.enrol.components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Student;

public class EnrolmentListItem {

	@Parameter
	@Property
	private Enrolment enrolment;

	@Parameter
	private Student student;

	@Parameter
	@Property
	private CourseClass courseClass;

	@Parameter
	@Property
	private int index;

	@Inject
	private ComponentResources componentResources;
	
	@Property
	private DateFormat dateFormat;

	@SetupRender
	void beforeRender() {
		dateFormat = new SimpleDateFormat("EEE d MMM yy h:mm a");
		dateFormat.setTimeZone(courseClass.getClassTimeZone());
	}

	public boolean isDisabled() {
		return !isEnrolmentSelected() && !canEnrol();
	}

	public boolean isEnrolled() {
		return enrolment.isDuplicated(student);
	}

	public boolean canEnrol() {
		return !isEnrolled() && courseClass.isHasAvailableEnrolmentPlaces();
	}

	public boolean isEnrolmentSelected() {
		return enrolment.getStudent() != null;
	}

	public void setEnrolmentSelected(boolean value) {
		if (value) {
			enrolment.setStudent(student);
		} else {
			enrolment.setStudent(null);
		}
	}

	public String getDisabledMessage() {
		if (isEnrolled()) {
			return "already enrolled";
		}
		if (!canEnrol()) {
			return "places unavailable";
		}
		return "";
	}

}
