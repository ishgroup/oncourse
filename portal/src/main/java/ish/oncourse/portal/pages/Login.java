package ish.oncourse.portal.pages;

import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.cookies.ICookiesService;
import org.apache.commons.lang.StringUtils;
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

	private static final String PARAMETER_firstName = "firstName";
	private static final String PARAMETER_lastName = "lastName";
	private static final String PARAMETER_emailAddress = "emailAddress";

	@Persist
	@Property
	private String email;

	@Persist
	@Property
	private String lastName;

	@Persist
	@Property
	private String companyName;

	@Persist
	@Property
	private Boolean isCompany;

	@Persist
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
	private ICookiesService cookieService;

	@InjectPage
	private Index index;

	@InjectPage
	private Timetable timetable;

	@InjectPage
	private ForgotPassword forgotPassword;

	@InjectPage
	private SelectCollege selectCollege;

	/**
	 * the property should be persist because we set it over ajax request and
	 * it should be available for the next requests
	 */
	@Persist
	private boolean isForgotPassword;

	@Inject
	private Messages messages;

	@InjectComponent(value = "loginForm")
	@Property
	private Form loginForm;

	@Persist
	@Property
	private String companyNameErrorMessage;

	@Persist
	@Property
	private String firstNameErrorMessage;

	@Persist
	@Property
	private String secondNameErrorMessage;

	@Persist
	@Property
	private String emailNameErrorMessage;

	@Persist
	@Property
	private String passwordNameErrorMessage;

	@Persist
	private Map<String, String> errors;

	private static final String UNABLE_TO_LOG = "You are unable to log into this site with this set of credentials. Please contact the college and let them know that there are two contacts with identical login details. If they merge those contacts, the problem will be resolved.";
	private static final String USER_NOT_EXIST = "User doesn't exist in ";
	private static final String HAS_ERROR = "has-error";
	private static final String DISPLAY_BLOCK = "display: block;";
	private static final String DISPLAY_NONE =  "display: none;";
	private static final String ON =  "on";
	private static final String AS_COMPANY =  "as company";
	private static final String AS_PERSON =  "as person";

	Object onActivate() {
		if (authenticationService.getUser() != null)
			authenticationService.logout();
		if (errors == null)
			errors = new HashMap<>();
		fillStudentFields();
		return null;
	}


	@AfterRender
	void afterRender() {
		clearErrorFields();
		isForgotPassword = false;
		errors.clear();

	}

	private void fillStudentFields() {
		String value = StringUtils.trimToNull(request.getParameter(PARAMETER_firstName));
		if (value != null)
			this.firstName = value;

		value = StringUtils.trimToNull(request.getParameter(PARAMETER_lastName));
		if (value != null)
			this.lastName = value;

		value = StringUtils.trimToNull(request.getParameter(PARAMETER_lastName));
		if (value != null)
			this.email = value;
	}

	@OnEvent(value = "onForgotPasswordEvent")
	Object onForgotPassword() {
		this.isForgotPassword = true;
		return loginForm;

	}

	Object onSuccess() throws IOException {
		clearErrorFields();


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

		if (!isForgotPassword) {
			if (StringUtils.isBlank(password)) {

				errors.put("password", messages.get("passwordNameErrorMessage"));

				passwordNameErrorMessage = messages.get("passwordNameErrorMessage");
				loginForm.recordError(passwordNameErrorMessage);
			}
		}


		if (!loginForm.getHasErrors()) {
			return (isForgotPassword) ? forgotPassword() : doLogin();
		}
		return this;
	}

	private void clearErrorFields() {
		emailNameErrorMessage = null;
		passwordNameErrorMessage = null;
		companyNameErrorMessage = null;
		secondNameErrorMessage = null;
		firstNameErrorMessage = null;
	}

	private Object doLogin() {
		List<Contact> users = new ArrayList<>();
		if (isCompany) {
			users = authenticationService.authenticateCompany(companyName, email, password);
		} else {
			users = authenticationService.authenticate(firstName, lastName, email, password);
		}

		if (users.isEmpty()) {
			emailNameErrorMessage = messages.get("emailNameErrorMessage");
			passwordNameErrorMessage = messages.get("passwordNameErrorMessage");
			companyNameErrorMessage = messages.get("companyNameErrorMessage");
			secondNameErrorMessage = messages.get("secondNameErrorMessage");
			firstNameErrorMessage = messages.get("firstNameErrorMessage");
			loginForm.recordError("Login unsucessful! Invalid login name or password");
			return this;
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
				loginForm.recordError(UNABLE_TO_LOG);
				return this;
			}

			selectCollege.setTheUsers(users, collegesWithDuplicates);
			return selectCollege;
		}
	}

	private Object forgotPassword() {

		List<Contact> users = new ArrayList<>();
		if (isCompany) {
			users = authenticationService.findCompanyForPasswordRecovery(companyName, email);
		} else {
			users = authenticationService.findForPasswordRecovery(firstName, lastName, email);
		}

		if (users.isEmpty()) {
			loginForm.recordError(USER_NOT_EXIST);
			return this;
		} else if (users.size() == 1) {
			forgotPassword.setUser(users.get(0));
			return forgotPassword;
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
				loginForm.recordError(UNABLE_TO_LOG);
				return this;
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
		return message != null ? HAS_ERROR : StringUtils.EMPTY;

	}





	public CompanyCheckStyle getCompanyCheckStyle() {
		return new CompanyCheckStyle();
	}


	public class CompanyCheckStyle {

		private boolean isCompany() {
			return isCompany != null && isCompany;
		}

		public String getCompanyDisplay() {

			return isCompany() ? DISPLAY_BLOCK : DISPLAY_NONE;
		}

		public String getStudentDisplay() {
			return isCompany() ? DISPLAY_NONE : DISPLAY_BLOCK;
		}

		public String getCompanyOn() {
			return isCompany() ? ON : StringUtils.EMPTY;
		}

		public String getLabel() {
			return isCompany() ? AS_COMPANY : AS_PERSON;
		}

	}
}
