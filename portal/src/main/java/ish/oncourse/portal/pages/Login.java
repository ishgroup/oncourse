package ish.oncourse.portal.pages;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Login {

	private static final String PARAMETER_companyName = "companyName";
	private static final String PARAMETER_firstName = "firstName";
	private static final String PARAMETER_lastName = "lastName";
	private static final String PARAMETER_email = "email";
	private static final String PARAMETER_password = "password";
	private static final String PARAMETER_firstTimeLogin = "e";

	private static final String PARAMETER_oneTimePassword = "oneTimePassword";

	@Inject
	private ICayenneService cayenneService;

	@Property
	private String email;

	@Property
	private boolean firstTimeLogin;

	@Property
	private String lastName;

	@Property
	private String companyName;

	@Property
	private Boolean isCompany;

	@Property
	private String firstName;

	@Property
	private String password;

	@Inject
	private Request request;

	@InjectComponent("email")
	private TextField emailField;

	@InjectComponent("lastName")
	private TextField lastNameField;

	@InjectComponent("firstName")
	private TextField firstNameField;

	@InjectComponent("password")
	private PasswordField passwordField;

	@InjectComponent("companyName")
	private TextField companyNameField;

	@Inject
	private IAuthenticationService authenticationService;

	@Inject
	private IPortalService portalService;

	@Inject
	private ICookiesService cookieService;

	@InjectPage
	private Index index;

	@InjectPage
	private Timetable timetable;

	@InjectPage
	private ForgotPassword forgotPassword;

	@InjectPage
	private CreateAccount createAccount;

	@InjectPage
	private SelectCollege selectCollege;

	/**
	 * the property should be persist because we set it over ajax request and
	 * it should be available for the next requests
	 */
	private boolean isForgotPassword;

	@Inject
	private Messages messages;

	@InjectComponent(value = "loginForm")
	@Property
	private Form loginForm;

	@Property
	private String companyNameErrorMessage;

	@Property
	private String firstNameErrorMessage;

	@Property
	private String secondNameErrorMessage;

	@Property
	private String emailNameErrorMessage;

	@Property
	private String passwordNameErrorMessage;

	private Map<String, String> errors  = new HashMap<>();

	private static final String ON = "on";

	Object onActivate() {
		if (portalService.getAuthenticatedUser() != null) {
			portalService.logout();
			return index;
		}

		if (errors == null)
			errors = new HashMap<>();

		String firstLoginEmail = StringUtils.trimToNull(request.getParameter(PARAMETER_firstTimeLogin));
		if (firstLoginEmail != null) {
			email = firstLoginEmail;
			Contact contact = getContactByUniqEmail(firstLoginEmail);
			if (contact != null) {
				firstTimeLogin = true;
				isCompany = contact.getIsCompany();
				firstName = contact.getGivenName();
				lastName = contact.getFamilyName();
				return null;
			}
			
		}

		String value = StringUtils.trimToNull(request.getParameter(PARAMETER_oneTimePassword));
		if (value != null) {
			if (authenticationService.authenticate(value)) {
				return index;
			} else {
				loginForm.recordError("The attempt for support login was unsuccessful.");
			}
		}

		fillStudentFields();
		return null;
	}

	private Contact getContactByUniqEmail(String email) {
		List<Contact> contacts = ObjectSelect.query(Contact.class).where(Contact.EMAIL_ADDRESS.eq(email)).select(cayenneService.newContext());
		if (contacts.size() == 1) {
			Contact contact = contacts.get(0);
			return contact.getPassword() == null ? contact : null;
		}
		return null;
	}

	@AfterRender
	void afterRender() {
		clearErrorFields();
		isForgotPassword = false;
		firstTimeLogin = false;
		errors.clear();

	}

	private void fillStudentFields() {
		String value = StringUtils.trimToNull(request.getParameter(PARAMETER_companyName));
		if (value != null)
			this.companyName = value;
		
		value = StringUtils.trimToNull(request.getParameter(PARAMETER_firstName));
		if (value != null)
			this.firstName = value;

		value = StringUtils.trimToNull(request.getParameter(PARAMETER_lastName));
		if (value != null)
			this.lastName = value;

		value = StringUtils.trimToNull(request.getParameter(PARAMETER_email));
		if (value != null)
			this.email = value;

		value = StringUtils.trimToNull(request.getParameter(PARAMETER_password));
		if (value != null)
			this.password = value;
	}
	
	@OnEvent(value = EventConstants.VALIDATE, component = "loginForm")
	void onValidate() throws IOException {
		if (isCompany) {
			if (StringUtils.isBlank(companyName)) {
				errors.put("companyName", messages.get("companyNameErrorMessage"));
				companyNameErrorMessage = messages.get("companyNameErrorMessage");
				loginForm.recordError(companyNameErrorMessage);
			}
		} else {
			if (StringUtils.isBlank(firstName)) {
				errors.put("firstName", messages.get("firstNameErrorMessage"));
				firstNameErrorMessage = messages.get("firstNameErrorMessage");
				loginForm.recordError(firstNameErrorMessage);
			}
			if (StringUtils.isBlank(lastName)) {
				errors.put("lastName", messages.get("secondNameErrorMessage"));
				secondNameErrorMessage = messages.get("secondNameErrorMessage");
				loginForm.recordError(secondNameErrorMessage);
			}

		}

		if (StringUtils.isBlank(email)) {
			errors.put("email", messages.get("emailNameErrorMessage"));
			emailNameErrorMessage = messages.get("emailNameErrorMessage");
			loginForm.recordError(emailNameErrorMessage);
		}

		if (!isForgotPassword && !firstTimeLogin) {
			if (StringUtils.isBlank(password)) {
				errors.put("password", messages.get("passwordNameErrorMessage"));
				passwordNameErrorMessage = messages.get("passwordNameErrorMessage");
				loginForm.recordError(passwordNameErrorMessage);
			}

			if (errors.isEmpty() && FindContact.valueOf(this).find().isEmpty()) {
				emailNameErrorMessage = messages.get("emailNameErrorMessage");
				passwordNameErrorMessage = messages.get("passwordNameErrorMessage");
				companyNameErrorMessage = messages.get("companyNameErrorMessage");
				secondNameErrorMessage = messages.get("secondNameErrorMessage");
				firstNameErrorMessage = messages.get("firstNameErrorMessage");
				loginForm.recordError("Login unsucessful! Invalid login name or password");
			}
		}
	}

	@OnEvent(value = EventConstants.SELECTED, component = "forgotPassword")
	void forgotPassword() {
		this.isForgotPassword = true;
	}

	@OnEvent(value = EventConstants.SELECTED, component = "createAccount")
	void createAccount() {
		this.firstTimeLogin = true;
	}
	
	Object onSuccess() throws IOException {
		if (isForgotPassword || firstTimeLogin) {
			return resetPassword();
		} else {
			return doLogin();
		}
	}

	private void clearErrorFields() {
		emailNameErrorMessage = null;
		passwordNameErrorMessage = null;
		companyNameErrorMessage = null;
		secondNameErrorMessage = null;
		firstNameErrorMessage = null;
	}

	private Object doLogin() {
		List<Contact> users = FindContact.valueOf(this).find();

		if (users.isEmpty()) {
			return null;
		} else if (users.size() == 1) {
			authenticationService.storeCurrentUser(users.get(0));
			URL prevPage = cookieService.popPreviousPageURL();
			return (prevPage != null) ? prevPage : index;
		} else {

			Set<College> colleges = new HashSet<>();
			Set<College> collegesWithDuplicates = new HashSet<>();

			// if in one college we have two or more contacts with identical login details
			// (we show the error on the login screen)
			for (Contact user : users) {
				if (!colleges.add(user.getCollege())) {
					// this college has already been added to the list, therefore there must be two or more duplicate contacts
					// we cannot determine which one is correct, so don't let either of them in
					collegesWithDuplicates.add(user.getCollege());
				}
			}

			colleges.removeAll(collegesWithDuplicates);

			if (colleges.size() < 1) {
				loginForm.recordError(messages.get("message-unableToLoginDuplicateContacts"));
				return null;
			}

			selectCollege.setTheUsers(users, collegesWithDuplicates);
			return selectCollege;
		}
	}

	private Object resetPassword() {
		List<Contact> users = FindContact.valueOf(this).findForPasswordRecovery();
		if (users.isEmpty()) {
			loginForm.recordError(messages.get("message-userNotExist"));
			return null;
		} else if (users.size() == 1) {
			if (firstTimeLogin) {
				createAccount.setUser(users.get(0).getId());
				return createAccount;
			} else {
				forgotPassword.setUser(users.get(0).getId());
				return forgotPassword;
			}
		} else {
			selectCollege.setPasswordRecover(true);

			Set<College> colleges = new HashSet<>();
			Set<College> collegesWithDuplicates = new HashSet<>();

			// if in one college we have two or more contacts with identical login details
			// (we show the error on the login screen)
			for (Contact user : users) {
				if (!colleges.add(user.getCollege())) {
					// this college has already been added to the list, therefore there must be two or more duplicate contacts
					// we cannot determine which one is correct, so don't let either of them in
					collegesWithDuplicates.add(user.getCollege());
				}
			}

			colleges.removeAll(collegesWithDuplicates);

			if (colleges.size() < 1) {
				loginForm.recordError(messages.get("message-unableToLoginDuplicateContacts"));
				return null;
			}

			selectCollege.setTheUsers(users, collegesWithDuplicates);
			return selectCollege;
		}
	}

	public String getValidationMessage(String fieldName) {
		if (errors == null)
			return StringUtils.EMPTY;
		String message = errors.get(fieldName);
		return message != null ? message : StringUtils.EMPTY;
	}

	public String getErrorClass(String fieldName) {
		if (errors == null)
			return StringUtils.EMPTY;
		String message = errors.get(fieldName);
		return message != null ? messages.get("div.class.error") : StringUtils.EMPTY;

	}


	public CompanyCheckStyle getCompanyCheckStyle() {
		return new CompanyCheckStyle();
	}


	public class CompanyCheckStyle {

		private boolean isCompany() {
			return isCompany != null && isCompany;
		}

		public String getCompanyDisplay() {

			return isCompany() ? messages.get("css.display.block") : messages.get("css.display.none");
		}

		public String getStudentDisplay() {
			return isCompany() ? messages.get("css.display.none") : messages.get("css.display.block");
		}

		public String getCompanyOn() {
			return isCompany() ? ON : StringUtils.EMPTY;
		}

		public String getLabel() {
			return isCompany() ? messages.get("label-asCompany") : messages.get("label-asPerson");
		}

	}


	private static class FindContact {
		private Login login;

		public List<Contact> find() {
			if (login.isCompany) {
				return login.authenticationService.authenticateCompany(login.companyName, login.email, login.password);
			} else {
				return login.authenticationService.authenticate(login.firstName, login.lastName, login.email, login.password);
			}
		}

		public List<Contact> findForPasswordRecovery() {
			if (login.isCompany) {
				return login.authenticationService.findCompanyForPasswordRecovery(login.companyName, login.email);
			} else {
				return login.authenticationService.findForPasswordRecovery(login.firstName, login.lastName, login.email);
			}
		}

		public static FindContact valueOf(Login login) {
			FindContact result = new FindContact();
			result.login = login;
			return result;
		}
	}

}


