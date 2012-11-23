package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.enrol.utils.EnrolCoursesModel;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Deprecated
public class EnrolmentListItem {

    static final Logger LOGGER = Logger.getLogger(EnrolmentListItem.class);
	@InjectPage
	private EnrolCourses enrolCourses;

	@Parameter
	@Property
	private int studentIndex;

	@Parameter
	@Property
	private int courseClassIndex;

	@Property
	@Persist
	private DateFormat dateFormat;

	@Property
	private CourseClass courseClass;

	@Property
	private Enrolment enrolment;
	
	private EnrolCoursesModel getModel() {
		return enrolCourses.getController().getModel();
	}

	@SetupRender
	void beforeRender() {
		courseClass = getModel().getClassesToEnrol().get(courseClassIndex);
		enrolment = getModel().getEnrolments()[studentIndex][courseClassIndex];
		dateFormat = new SimpleDateFormat("EEE d MMM yy h:mm a");

		TimeZone classTimeZone = courseClass.getClassTimeZone();
		if (classTimeZone != null) {
			dateFormat.setTimeZone(classTimeZone);
		}
	}

	public boolean isDisabled() {
		return !isEnrolmentSelected() && !canEnrol();
	}

	public boolean isCommenced() {
		return courseClass.hasStarted() && !courseClass.hasEnded();
	}

	public boolean isEnrolmentSelected() {
		return enrolment.getInvoiceLine() != null;
	}

	public String getDisabledMessage() {
		if (isDuplicated()) {
			return "already enrolled";
		}
		if (!hasAvailablePlaces()) {
			return "places unavailable";
		}
		if (courseClass.hasEnded()) {
			return "This class is finished";
		}
		return "";
	}

	/**
	 * Checks if the enrolment with the same student and class already exists.
	 * 
	 * @return true if the enrolment under consideration is duplicated.
	 */
	private boolean isDuplicated() {

		return enrolment.isDuplicated();
	}

	/**
	 * Checks if the courseClass under consideration has available places.
	 * 
	 * @return true if there are available places for enrolment.
	 */
	private boolean hasAvailablePlaces() {
		return courseClass.isHasAvailableEnrolmentPlaces();
	}

	/**
	 * Checks if the enrolment is possible, ie if it is not duplicated and if
	 * there are available places in class.
	 * 
	 * @return
	 */
	public boolean canEnrol() {
		return !isDuplicated() && hasAvailablePlaces() && !courseClass.hasEnded();
	}
}
