package ish.oncourse.enrol.components;

import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class EnrolmentContactEntry {

	/**
	 * Contants
	 */
	private static final String VALID_CLASS = "valid";

	private static final String VALIDATE_CLASS = "validate";

	/**
	 * ish services
	 */
	@Inject
	private IWebSiteService webSiteService;

	
	@Inject
	private IStudentService studentService;

	/**
	 * tapestry services
	 */
	@Inject
	private Request request;

	@Inject
	private Messages messages;

	/**
	 * components
	 */
	@InjectComponent
	@Property
	private Zone addStudentBlock;

	@InjectComponent
	@Property
	private Form shortDetailsForm;

	@InjectComponent
	private TextField firstName;

	@InjectComponent
	private TextField lastName;

	@InjectComponent
	private TextField email;

	/**
	 * properties
	 */
	@Property
	private boolean needMoreInfo;

	@Persist
	@Property
	private boolean hasContact;

	@Parameter
	@Property
	private boolean showConcessionsArea;

	@Property
	@Persist
	private Contact contact;

	@Parameter
	private ObjectContext context;

	private boolean reset;

	/**
	 * these properties are used for validation list and for hints
	 */
	@Property
	private String firstNameErrorMessage;

	@Property
	private String lastNameErrorMessage;

	@Property
	private String emailErrorMessage;

	@SetupRender
	void beforeRender() {
		hasContact = false;
		contact = context.newObject(Contact.class);

		College currentCollege = webSiteService.getCurrentCollege();
		College college = (College) context.localObject(
				currentCollege.getObjectId(), currentCollege);
		contact.setCollege(college);

		Student student = context.newObject(Student.class);
		student.setCollege(college);
		contact.setStudent(student);
		
		contact.setIsMale(true);
		contact.setIsMarketingViaEmailAllowed(true);
		contact.setIsMarketingViaPostAllowed(true);
		contact.setIsMarketingViaSMSAllowed(true);
		
		reset = true;
		shortDetailsForm.clearErrors();
	}

	public String getAddStudentBlockClass() {
		List<Student> shortlistStudents = (List<Student>) request.getSession(
				false).getAttribute("shortlistStudents");
		return (shortlistStudents == null || shortlistStudents.isEmpty() || needMoreInfo) ? "show"
				: "collapse";
	}

	public boolean isNewStudent() {
		return contact.getPersistenceState() == PersistenceState.NEW;
	}

	@OnEvent(component = "addStudentAction", value = "selected")
	void onSelectedFromAddStudentAction() {
		reset = false;
	}

	@OnEvent(component = "resetAll", value = "selected")
	void onSelectedFromReset() {
		reset = true;
	}

	@OnEvent(component = "shortDetailsForm", value = "validate")
	void validate() {
		if (reset) {
			shortDetailsForm.clearErrors();
		} else {
			firstNameErrorMessage = contact.validateGivenName();
			if (firstNameErrorMessage != null) {
				shortDetailsForm.recordError(firstName, firstNameErrorMessage);
			}
			lastNameErrorMessage = contact.validateFamilyName();
			if (lastNameErrorMessage != null) {
				shortDetailsForm.recordError(lastName, lastNameErrorMessage);
			}
			emailErrorMessage = contact.validateEmail();
			if (emailErrorMessage != null) {
				shortDetailsForm.recordError(email, emailErrorMessage);
			}
		}
	}

	@OnEvent(component = "shortDetailsForm", value = "failure")
	Block refreshContactEntry() {
		return addStudentBlock.getBody();
	}

	@OnEvent(component = "shortDetailsForm", value = "success")
	Object submittedSuccessfully() {
		if (reset) {
			contact.setGivenName(null);
			contact.setFamilyName(null);
			contact.setEmailAddress(null);

		} else {
			Student student = studentService.getStudent(contact.getGivenName(),
					contact.getFamilyName(), contact.getEmailAddress());
			if (student != null) {
				contact = student.getContact();
				studentService.addStudentToShortlist(student);
				return "EnrolCourses";
			}
			hasContact = true;
		}
		return addStudentBlock.getBody();
	}

	public String getFirstNameInput() {
		return getInputSectionClass(firstName);
	}

	public String getLastNameInput() {
		return getInputSectionClass(lastName);
	}

	public String getEmailInput() {
		return getInputSectionClass(email);
	}

	private String getInputSectionClass(TextField field) {
		ValidationTracker defaultTracker = shortDetailsForm.getDefaultTracker();
		return defaultTracker == null || !defaultTracker.inError(field) ? VALID_CLASS
				: VALIDATE_CLASS;
	}

}
