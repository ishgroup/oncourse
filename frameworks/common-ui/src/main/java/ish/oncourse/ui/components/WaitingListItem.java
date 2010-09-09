package ish.oncourse.ui.components;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Student;
import ish.oncourse.model.WaitingList;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class WaitingListItem {

	@Parameter
	@Property
	private CourseClass courseClass;

	@Parameter
	@Property
	private Course course;

	@Property
	private boolean showingForm;

	// TODO replace this feedback note by tapestry validation
	@Property
	private String feedBack;

	@Property
	@Persist
	private WaitingList waitingList;

	@Property
	@Persist
	private WaitingList sessionWaitingList;

	@SetupRender
	public void beforeRender() {
		waitingList = getWaitingListForCourse();
		sessionWaitingList = getSessionWaitingListForCourse();
	}

	public boolean isHasConfirmationEmail() {
		return false;
	}

	public String getConfirmationEmail() {
		return "";
	}

	public boolean isStudentInWaitingList() {
		return sessionWaitingList != null;
	}

	public boolean isHasPotentialStudents() {
		return getPotentialStudentsCount() > 1;
	}

	public boolean isHasPotentialStudentsForCourse() {
		return waitingList.getPotentialStudents() > 1;
	}

	public Integer getPotentialStudentsCount() {
		return sessionWaitingList.getPotentialStudents();
	}

	public boolean isHasContactForRequest() {
		return false;
	}

	public boolean isHasFeedback() {
		return false;
	}

	public boolean isHasCourseClass() {
		return courseClass != null;
	}

	public boolean isHasMoreClasses() {
		return course.getEnrollableClasses().size() > 1;
	}

	public boolean isHasMoreAvailablePlaces() {
		int places = 0;
		for (CourseClass courseClass : course.getEnrollableClasses()) {
			places += courseClass.getAvailableEnrolmentPlaces();
		}
		return places > 0;
	}

	public WaitingList getWaitingListForCourse() {
		// TODO get current user contact from request
		Student student = null;

		if (student == null || course == null) {
			return null;
		}
		try {
			return student.getActiveWaitingListForCourse( course );
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	public WaitingList getSessionWaitingListForCourse() {
		// TODO get current user contact from session
		Student student = null;

		if (student == null || course == null) {
			return null;
		}
		try {
			 return student.getActiveWaitingListForCourse( course );
		} catch (Exception e) {
			// ignore
		}
		return null;
	}
}
