package ish.oncourse.enrol.components;

import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.model.services.persistence.ICayenneService;
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
	private ICayenneService cayenneService;

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

	@Property
	private boolean hasContact;

	@Parameter
	@Property
	private boolean showConcessionsArea;

	@Property
	@Persist
	private Contact contact;

	@Persist
	private ObjectContext context;
	
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
		context = cayenneService.newContext();

		contact = context.newObject(Contact.class);
		College college = webSiteService.getCurrentCollege();
		contact.setCollege((College) context.localObject(college.getObjectId(),
				college));
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

	private boolean reset;

	@OnEvent(component = "resetAll", value = "selected")
	void onSelectedFromReset() {
		reset = true;
	}

	@OnEvent(component = "shortDetailsForm", value = VALIDATE_CLASS)
	void validate() {
		if (reset) {
			shortDetailsForm.clearErrors();
		} else {
			if (contact.getGivenName() == null
					|| "".equals(contact.getGivenName())) {
				firstNameErrorMessage = messages.get("first-name-required");
				shortDetailsForm.recordError(firstName, firstNameErrorMessage);
			}
			if (contact.getFamilyName() == null
					|| "".equals(contact.getFamilyName())) {
				lastNameErrorMessage = messages.get("last-name-required");
				shortDetailsForm.recordError(lastName, lastNameErrorMessage);
			}
			if (contact.getEmailAddress() == null
					|| "".equals(contact.getEmailAddress())) {
				emailErrorMessage = messages.get("email-required");
				shortDetailsForm.recordError(email, emailErrorMessage);
			} else {
				if (!contact.getEmailAddress().matches(
						messages.get("email-regexp"))) {
					emailErrorMessage = messages.get("email-regexp-message");
					shortDetailsForm.recordError(email, emailErrorMessage);
				}
			}

		}
	}

	@OnEvent(component = "shortDetailsForm", value = "submit")
	Block refreshContactEntry() {
		return addStudentBlock.getBody();
	}

	@OnEvent(component = "shortDetailsForm", value = "success")
	void submittedSuccessfully() {
		if (reset) {
			contact.setGivenName(null);
			contact.setFamilyName(null);
			contact.setEmailAddress(null);

		} else {
			Student student = studentService.getStudent(contact.getGivenName(),
					contact.getFamilyName(), contact.getEmailAddress());
			if (student != null) {
				contact = student.getContact();
			}
			hasContact = true;
		}

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
		return defaultTracker==null||!defaultTracker.inError(field) ? VALID_CLASS
				: VALIDATE_CLASS;
	}

}
