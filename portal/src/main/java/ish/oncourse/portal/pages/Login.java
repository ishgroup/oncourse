package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.cookies.ICookiesService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Login {

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
	private Boolean iscompany;
	
	@Persist
	@Property
	private String firstName;

	@Property
	private String password;

	@Component
	private Form loginForm;

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
	private ForgotPassword forgotPassword;

	@InjectPage
	private SelectCollege selectCollege;

	private boolean isForgotPassword;

	@SetupRender
	void setupRender() {
		// perform logout to cleanup the session before the new login
		authenticationService.logout();
	}

	@OnEvent(component = "forgotPassword", value = "selected")
	void onSelectedForgotPassword() {
		this.isForgotPassword = true;
	}

	Object onSuccess() throws IOException {

		if (StringUtils.isBlank(email)) {
			loginForm.recordError(emailField, "Please enter your email");
		}

		if (!isForgotPassword) {
			if (StringUtils.isBlank(password)) {
				loginForm.recordError(passwordField, "Please enter your password");
			}
		}

		if (iscompany) {
			if (StringUtils.isBlank(companyName)) {
				loginForm.recordError(companyNameField, "Please enter your company name");
			}
		} else {
			if (StringUtils.isBlank(lastName)) {
				loginForm.recordError(lastNameField, "Please enter your last name");
			}
			if (StringUtils.isBlank(firstName)) {
				loginForm.recordError(firstNameField, "Please enter your first name");
			}
		}

		if (!loginForm.getHasErrors()) {
			return (isForgotPassword) ? forgotPassword() : doLogin();
		}

		return this;
	}

	private Object doLogin() {
		List<Contact> users = new ArrayList<Contact>();
		if(iscompany) {
			users= authenticationService.authenticateCompany(companyName, email, password);
		} else {
			users = authenticationService.authenticate(firstName, lastName, email, password);
		}
		
		if (users.isEmpty()) {
			loginForm.recordError("Login unsucessful! Invalid login name or password");
			return this;
		} else if (users.size() == 1) {
			authenticationService.storeCurrentUser(users.get(0));
			URL prevPage = cookieService.popPreviousPageURL();
			return (prevPage != null) ? prevPage : index;
		} else {
			
			// if in one college we have two or more contacts with identical login details
			// (we show the error on the login screen)
			for (Contact user : users) {
				for (Contact userForCheck : users) {
					if(!user.equals(userForCheck)){
						// check if 2 users with identical login details have identical college
						if(user.getCollege().equals(userForCheck.getCollege())){
							loginForm.recordError("You are unable to log into this site with this set of credentials. Please contact the college and let them know that there are two contacts with identical login details. If they merge those contacts, the problem will be resolved.");
							return this;
						}
					}
				}
			}
			
			selectCollege.setTheUsers(users);
			return selectCollege;
		}
	}
	
	private Object forgotPassword() {

		List<Contact> users = new ArrayList<Contact>();
		if(iscompany) {
			users= authenticationService.authenticateCompany(companyName, email, password);
		} else {
			users = authenticationService.findForPasswordRecovery(firstName, lastName, email);
		}

		if (users.isEmpty()) {
			loginForm.recordError("User doesn't exist in ");
			return this;
		} else if (users.size() == 1) {
			forgotPassword.setUser(users.get(0));
			return forgotPassword;
		} else {
			selectCollege.setPasswordRecover(true);
			selectCollege.setTheUsers(users);
			return selectCollege;
		}
	}
}
