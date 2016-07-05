package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Session;
import ish.oncourse.model.Student;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.ArrayList;
import java.util.List;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 3:22 PM
 */
public class ClassRollNew {

	@Property
	@Parameter
	private CourseClass courseClass;

	@Property
	private Enrolment enrolment;

	@Property
	private List<Enrolment> enrolments;

	@Property
	private List<Session> sessions;

	@Property
	private Session session;

	@SetupRender
	boolean setupRender() {

		enrolments = courseClass.getValidEnrolments();
		sessions = courseClass.getSessions();
		return true;
	}
	
	public int getNumberOfStudents(){
		return courseClass.validEnrolmentsCount();
	}
}
