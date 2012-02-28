package ish.oncourse.enrol.components;

import ish.common.types.PaymentStatus;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
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
	 * ish services
	 */
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IStudentService studentService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IConcessionsService concessionsService;

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
	private TextField firstNameField;

	@InjectComponent
	private TextField lastNameField;

	@InjectComponent
	private TextField emailField;

	@Property
	private String firstName;

	@Property
	private String lastName;

	@Property
	private String email;

	/**
	 * properties
	 */
	@Property
	private boolean needMoreInfo;

	@Property
	@Persist
	private Contact contact;

	/**
	 * Reset form or not.
	 */
	private boolean reset;

	@Persist
	@Property
	private boolean hasContact;

	/**
	 * these properties are used for validation list and for hints
	 */
	@Property
	private String firstNameErrorMessage;

	@Property
	private String lastNameErrorMessage;

	@Property
	private String emailErrorMessage;

	@Inject
	private Request request;

	@SetupRender
	void beforeRender() {
		this.reset = true;
		this.hasContact = false;
		this.contact = null;
		shortDetailsForm.clearErrors();
	}

	public String getAddStudentBlockClass() {
		List<Long> shortlistStudents = studentService.getContactsIdsFromShortList();
		return (shortlistStudents == null || shortlistStudents.isEmpty() || needMoreInfo) ? "show" : "collapse";
	}

	public boolean isNewStudent() {
		return (contact != null) && (contact.getPersistenceState() == PersistenceState.NEW);
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
			firstNameErrorMessage = Contact.validateGivenName("student", firstName);
			if (firstNameErrorMessage != null) {
				shortDetailsForm.recordError(firstNameField, firstNameErrorMessage);
			}
			lastNameErrorMessage = Contact.validateFamilyName("student", lastName);
			if (lastNameErrorMessage != null) {
				shortDetailsForm.recordError(lastNameField, lastNameErrorMessage);
			}
			emailErrorMessage = Contact.validateEmail("student", email);
			if (emailErrorMessage != null) {
				shortDetailsForm.recordError(emailField, emailErrorMessage);
			}

		}
	}

	@OnEvent(component = "shortDetailsForm", value = "failure")
	Block refreshContactEntry() {
		return addStudentBlock.getBody();
	}

	@OnEvent(component = "shortDetailsForm", value = "success")
	Object submittedSuccessfully() {
		Object nextPage = addStudentBlock.getBody();

		if (reset) {
			this.firstName = null;
			this.lastName = null;
			this.email = null;
		} else {
			Contact studentContact = studentService.getStudentContact(firstName, lastName, email);

			ObjectContext context = cayenneService.newContext();

			if (studentContact != null) {
				this.contact = (Contact) context.localObject(studentContact.getObjectId(), null);
				if (contact.getStudent() == null) {
					contact.createNewStudent();
					context.commitChanges();
				}
				completeInTransactionPayments(contact);
				studentService.addStudentToShortlist(contact);
				nextPage = "EnrolCourses";
			} else {
				this.contact = context.newObject(Contact.class);

				College college = (College) context.localObject(webSiteService.getCurrentCollege().getObjectId(), null);
				contact.setCollege(college);

				contact.setGivenName(firstName);
				contact.setFamilyName(lastName);
				contact.setEmailAddress(email);

				contact.createNewStudent();
				contact.setIsMarketingViaEmailAllowed(true);
				contact.setIsMarketingViaPostAllowed(true);
				contact.setIsMarketingViaSMSAllowed(true);
			}
			this.hasContact = true;
		}
		
		// Show add new student
		return nextPage;
	}

	public String getFirstNameInput() {
		return getInputSectionClass(firstNameField);
	}

	public String getLastNameInput() {
		return getInputSectionClass(lastNameField);
	}

	public String getEmailInput() {
		return getInputSectionClass(emailField);
	}

	private String getInputSectionClass(TextField field) {
		ValidationTracker defaultTracker = shortDetailsForm.getDefaultTracker();
		return defaultTracker == null || !defaultTracker.inError(field) ? messages.get("validInput") : messages.get("validateInput");
	}

	public boolean isShowConcessionsArea() {
		return concessionsService.hasActiveConcessionTypes();
	}
	
	/**
	 * Check if there are in_transaction payments on enroling contact. If finds any it abandons them.
	 * @param contact enroling contact
	 */
	private void completeInTransactionPayments(Contact contact) {
		ObjectContext context = cayenneService.newContext();
		
		SelectQuery q = new SelectQuery(PaymentIn.class);
		q.andQualifier(ExpressionFactory.inExp(PaymentIn.STATUS_PROPERTY, PaymentStatus.IN_TRANSACTION, PaymentStatus.CARD_DETAILS_REQUIRED));
		q.andQualifier(ExpressionFactory.matchExp(PaymentIn.CONTACT_PROPERTY, contact));
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW);
		q.andQualifier(ExpressionFactory.greaterExp(PaymentIn.CREATED_PROPERTY, calendar.getTime()));
		
		List<PaymentIn> payments = context.performQuery(q);
		
		for (PaymentIn p : payments) {
			p.abandonPayment();
		}
		
		context.commitChanges();
	}
}
