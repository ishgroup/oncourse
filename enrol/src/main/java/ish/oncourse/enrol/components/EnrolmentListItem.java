package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Student;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EnrolmentListItem {

	@Property
	private static final String SEPARATOR = "_";

	private static final String DIGIT_PATTERN = "(\\d)+";

	@Parameter
	private int studentIndex;

	@Parameter
	private int courseClassIndex;
	
	@Property
	private int sIndex;

	@Property
	private int cCIndex;
	
	@Inject
	private ComponentResources componentResources;
	
	@Property
	@Persist
	private DateFormat dateFormat;
	
	@InjectComponent
	@Property
	private Form enrolmentCheckboxForm;

	@InjectComponent
	private Checkbox enrolmentCheckbox;
	
	@Persist
	private EnrolCourses enrolCoursesPage;
	
	@SetupRender
	void beforeRender() {
		dateFormat = new SimpleDateFormat("EEE d MMM yy h:mm a");
		enrolCoursesPage=(EnrolCourses)componentResources.getPage();
		sIndex=studentIndex;
		cCIndex=courseClassIndex;
		dateFormat.setTimeZone(getCourseClass().getClassTimeZone());
	}

	public int getIndex() {
		return studentIndex * enrolCoursesPage.getCourseClasses().size() + courseClassIndex;
	}

	/**
	 * Before performing checking/unchecking, first of all it looks if the checkbox is enabled, 
	 * that's why the updating of indexes is performing here
	 * @return
	 */
	public boolean isDisabled() {
		updateItemIndexes();
		return !isEnrolmentSelected() && !canEnrol();
	}

	private void updateItemIndexes() {
		String[]indexes=enrolmentCheckbox.getControlName().split(SEPARATOR);
		String sIndexStr = indexes[indexes.length-2];
		String cCIndexStr = indexes[indexes.length-1];
		if(sIndexStr.matches(DIGIT_PATTERN)&&cCIndexStr.matches(DIGIT_PATTERN)){
			sIndex=Integer.parseInt(sIndexStr);
			cCIndex=Integer.parseInt(cCIndexStr);
		}
	}

	public boolean isEnrolled() {
		return getEnrolment().isDuplicated(getStudent());
	}

	public boolean canEnrol() {
		return !isEnrolled() && getCourseClass().isHasAvailableEnrolmentPlaces();
	}

	public boolean isEnrolmentSelected() {
		return getEnrolment().getInvoiceLine() != null;
	}

	public void setEnrolmentSelected(boolean value) {
		
		if (value) {
			getEnrolment().setInvoiceLine(getInvoiceLine());
		} else {
			getEnrolment().setInvoiceLine(null);
		}
	
	}
	
	@OnEvent(component="enrolmentCheckboxForm", value="submit")
	Object enrolmentChClicked(){
		return enrolCoursesPage.enrolmentsUpdated();
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

	public Enrolment getEnrolment(){
		return enrolCoursesPage.getEnrolments()[sIndex][cCIndex];
	}
	
	public InvoiceLine getInvoiceLine() {
		return enrolCoursesPage.getInvoiceLines()[sIndex][cCIndex];
	}
	public Student getStudent() {
		return enrolCoursesPage.getContacts().get(sIndex).getStudent();
	}

	public CourseClass getCourseClass() {
		return enrolCoursesPage.getCourseClasses().get(cCIndex);
	}
}
