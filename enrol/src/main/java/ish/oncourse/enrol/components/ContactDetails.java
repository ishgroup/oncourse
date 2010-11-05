package ish.oncourse.enrol.components;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

public class ContactDetails {
	/**
	 * Contants
	 */
	private static final String VALID_CLASS = "valid";

	private static final String VALIDATE_CLASS = "validate";

	private static final DateFormat FORMAT = new SimpleDateFormat("d/M/y");

	/**
	 * tapestry services
	 */
	@Inject
	private Messages messages;

	@Inject
	private Request request;

	/**
	 * Parameters
	 */
	@Property
	@Parameter(value = "required")
	private Contact contact;

	@Property
	@Parameter
	private boolean showEmail;

	@Parameter
	@Property
	private boolean showConcessionsArea;

	@Property
	@Parameter
	private boolean requireAdditionalInfo;

	@Parameter
	private Zone parentZone;

	/**
	 * components
	 */
	@InjectComponent
	private Form contactDetailsForm;

	@InjectComponent
	private TextField email;

	@InjectComponent
	private TextField suburb;

	@InjectComponent
	private TextField postcode;

	@InjectComponent
	private TextField state;

	@InjectComponent
	private TextField homePhone;

	@InjectComponent
	private TextField mobilePhone;

	@InjectComponent
	private TextField businessPhone;

	@InjectComponent
	private TextField fax;

	@InjectComponent
	private PasswordField password;

	@InjectComponent
	private PasswordField passwordConfirm;

	@InjectComponent
	private TextField birthDate;

	/**
	 * template properties
	 */
	@Property
	private String passwordConfirmProperty;

	private String birthDateProperty;

	/**
	 * error message template properties
	 */
	@Property
	private String emailErrorMessage;

	@Property
	private String suburbErrorMessage;

	@Property
	private String postcodeErrorMessage;

	@Property
	private String stateErrorMessage;

	@Property
	private String homePhoneErrorMessage;

	@Property
	private String mobilePhoneErrorMessage;

	@Property
	private String businessPhoneErrorMessage;

	@Property
	private String faxErrorMessage;

	@Property
	private String passwordErrorMessage;

	@Property
	private String passwordConfirmErrorMessage;

	@Property
	private String birthDateErrorMessage;

	/**
	 * reset form method flag
	 */
	private boolean reset;

	@SetupRender
	void beforeRender() {
		// Add operations before render
	}

	@OnEvent(component = "contactDetailsForm", value = "failure")
	Block submitFailed() {
		return parentZone.getBody();
	}

	@OnEvent(component = "contactDetailsForm", value = "success")
	Object submitted() {
		if (reset) {
			contact.setStreet(null);
			contact.setSuburb(null);
			contact.setPostcode(null);
			contact.setState(null);
			contact.setHomePhoneNumber(null);
			contact.setMobilePhoneNumber(null);
			contact.setBusinessPhoneNumber(null);
			contact.setFaxNumber(null);
			contact.setPassword(null);
			contact.setDateOfBirth(null);
			contact.setIsMale(true);
			contact.setIsMarketingViaEmailAllowed(true);
			contact.setIsMarketingViaPostAllowed(true);
			contact.setIsMarketingViaSMSAllowed(true);
			// TODO add the student's info clearing after the student-specific
			// view will be implemented
			return parentZone.getBody();
		} else {
			// TODO uncomment when the db issue will be fixed
			// contact.getObjectContext().commitChanges();
			Session session = request.getSession(false);
			List<Student> students = (List<Student>) session
					.getAttribute("shortlistStudents");
			if (students == null) {
				students = new ArrayList<Student>();
			}
			students.add(contact.getStudent());
			session.setAttribute("shortlistStudents", students);
			return "EnrolCourses";
		}
	}

	@OnEvent(component = "addDetailsAction", value = "selected")
	void onSelectedFromAddStudentAction() {
		reset = false;
	}

	@OnEvent(component = "resetDetails", value = "selected")
	void onSelectedFromReset() {
		reset = true;
	}

	@OnEvent(component = "contactDetailsForm", value = "validate")
	void validate() {
		if (reset) {
			contactDetailsForm.clearErrors();
		} else {
			emailErrorMessage = contact.validateEmail();
			if (emailErrorMessage != null) {
				contactDetailsForm.recordError(email, emailErrorMessage);
			}
			suburbErrorMessage = contact.validateSuburb();
			if (suburbErrorMessage != null) {
				contactDetailsForm.recordError(suburb, suburbErrorMessage);
			}
			postcodeErrorMessage = contact.validatePostcode();
			if (postcodeErrorMessage != null) {
				contactDetailsForm.recordError(postcode, postcodeErrorMessage);
			}
			stateErrorMessage = contact.validateState();
			if (stateErrorMessage != null) {
				contactDetailsForm.recordError(state, stateErrorMessage);
			}
			homePhoneErrorMessage = contact.validateHomePhone();
			if (homePhoneErrorMessage != null) {
				contactDetailsForm
						.recordError(homePhone, homePhoneErrorMessage);
			}
			mobilePhoneErrorMessage = contact.validateMobilePhone();
			if (mobilePhoneErrorMessage != null) {
				contactDetailsForm.recordError(mobilePhone,
						mobilePhoneErrorMessage);
			}
			businessPhoneErrorMessage = contact.validateBusinessPhone();
			if (businessPhoneErrorMessage != null) {
				contactDetailsForm.recordError(businessPhone,
						businessPhoneErrorMessage);
			}
			faxErrorMessage = contact.validateFax();
			if (faxErrorMessage != null) {
				contactDetailsForm.recordError(fax, faxErrorMessage);
			}
			passwordErrorMessage = contact.validatePassword();
			if (passwordErrorMessage != null) {
				contactDetailsForm.recordError(password, passwordErrorMessage);
			}
			passwordConfirmErrorMessage = contact
					.validatePasswordConfirm(passwordConfirmProperty);
			if (passwordConfirmErrorMessage != null) {
				contactDetailsForm.recordError(passwordConfirm,
						passwordConfirmErrorMessage);
			}
			if (birthDateErrorMessage == null) {
				birthDateErrorMessage = contact.validateBirthDate();
			}
			if (birthDateErrorMessage != null) {
				contactDetailsForm
						.recordError(birthDate, birthDateErrorMessage);
			}
		}
	}

	public String getEmailInputClass() {
		return getInputSectionClass(email);
	}

	public String getSuburbInputClass() {
		return getInputSectionClass(suburb);
	}

	public String getPostcodeInputClass() {
		return getInputSectionClass(postcode);
	}

	public String getStateInputClass() {
		return getInputSectionClass(state);
	}

	public String getHomePhoneInputClass() {
		return getInputSectionClass(homePhone);
	}

	public String getMobilePhoneInputClass() {
		return getInputSectionClass(mobilePhone);
	}

	public String getBusinessPhoneInputClass() {
		return getInputSectionClass(businessPhone);
	}

	public String getFaxInputClass() {
		return getInputSectionClass(fax);
	}

	public String getPasswordInputClass() {
		return getInputSectionClass(password);
	}

	public String getPasswordConfirmInputClass() {
		return getInputSectionClass(passwordConfirm);
	}

	public String getBirthDateInputClass() {
		return getInputSectionClass(birthDate);
	}

	private String getInputSectionClass(Field field) {
		ValidationTracker defaultTracker = contactDetailsForm
				.getDefaultTracker();
		return defaultTracker == null || !defaultTracker.inError(field) ? VALID_CLASS
				: VALIDATE_CLASS;
	}

	public String getBirthDateProperty() {
		Date dateOfBirth = contact.getDateOfBirth();
		if (dateOfBirth == null) {
			return null;
		}
		return FORMAT.format(dateOfBirth);
	}

	public void setBirthDateProperty(String birthDateProperty) {
		try {
			if (birthDateProperty != null && !"".equals(birthDateProperty)) {
				Date parsedDate = FORMAT.parse(birthDateProperty);
				contact.setDateOfBirth(parsedDate);
			}
		} catch (ParseException e) {
			birthDateErrorMessage = "Please enter a valid date of birth and formatted as indicated: in the form 25/12/2000";
		}
	}

	public boolean isShowAdditionalInfoMessage() {
		return requireAdditionalInfo && contactDetailsForm.isValid();
	}

	public boolean isNewStudent() {
		return contact.getStudent().getPersistenceState() == PersistenceState.NEW;
	}
}
