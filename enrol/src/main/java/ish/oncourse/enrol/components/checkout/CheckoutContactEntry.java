package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.net.MalformedURLException;
import java.net.URL;

public class CheckoutContactEntry {
	
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

	@SuppressWarnings("all")
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
	
	@Parameter
	private String nextPageName;

	@Inject
	private Request request;
	
	@InjectPage
    private Checkout checkoutPage;
	
	private PurchaseController getController() {
		return checkoutPage.getPurchaseController();
	}

	@SetupRender
	void beforeRender() {
		this.reset = true;
		this.hasContact = false;
		this.contact = null;
		shortDetailsForm.clearErrors();
		if (nextPageName == null) {
			//if param not passed use the default checkout page value
			nextPageName = Checkout.class.getSimpleName();
		}
	}

	public String getAddStudentBlockClass() {
		return needMoreInfo ? "show" : "collapse";
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
	Object submittedSuccessfully() throws MalformedURLException {
        if (!request.isXHR())
            return new URL(request.getServerName());
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
				if (getController() != null) {
					ActionParameter actionParameter = new ActionParameter(Action.ADD_CONTACT);
					actionParameter.setValue(contact);
					getController().performAction(actionParameter);
				}
				nextPage = nextPageName;
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
}
