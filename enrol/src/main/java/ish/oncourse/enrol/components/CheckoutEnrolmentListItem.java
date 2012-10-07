package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.enrol.utils.PurchaseModel;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class CheckoutEnrolmentListItem {
	static final Logger LOGGER = Logger.getLogger(CheckoutEnrolmentListItem.class);
	@InjectPage
	private Checkout checkoutPage;

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
	
	private PurchaseModel getModel() {
		return getController().getModel();
	}
	
	private PurchaseController getController() {
		return checkoutPage.getController();
	}

	@SetupRender
	void beforeRender() {
		courseClass = getController().getModel().getClasses().get(courseClassIndex);
		Contact currentStudent = getModel().getContacts().get(studentIndex);
		enrolment = getModel().getEnrolmentByCourseClass(currentStudent, courseClass);
		dateFormat = new SimpleDateFormat("EEE d MMM yy h:mm a");
		TimeZone classTimeZone = courseClass.getClassTimeZone();
		if (classTimeZone != null) {
			dateFormat.setTimeZone(classTimeZone);
		}
	}

	public boolean isDisabled() {
		return !canEnrol() && !isEnrolmentSelected();
	}

	public boolean isCommenced() {
		return courseClass.hasStarted() && !courseClass.hasEnded();
	}

	public boolean isEnrolmentSelected() {
		return getModel().isEnrolmentEnabled(enrolment);
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
		return StringUtils.EMPTY;
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
