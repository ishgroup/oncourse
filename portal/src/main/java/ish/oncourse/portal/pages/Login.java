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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Inject
    private Messages messages;

//    @InjectComponent
//    private Zone loginZone;

    @InjectComponent
    @Property
    private Form loginForm;

    @Persist
    private String companyNameErrorMessage;

    @Persist
    private String firstNameErrorMessage;

    @Persist
    private String secondNameErrorMessage;

    @Persist
    private String emailNameErrorMessage;

    @Persist
    private String passwordNameErrorMessage;



//    public Zone getLoginZone() {
//        return loginZone;
//    }

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

        clearErrorFields();

		if (StringUtils.isBlank(email)) {
            emailNameErrorMessage = messages.get("emailNameErrorMessage");
			loginForm.recordError(emailField, emailNameErrorMessage);
		}

		if (!isForgotPassword) {
			if (StringUtils.isBlank(password)) {

                passwordNameErrorMessage = messages.get("passwordNameErrorMessage");
                loginForm.recordError(passwordField, passwordNameErrorMessage);
			}
		}

		if (iscompany) {
			if (StringUtils.isBlank(companyName)) {
                companyNameErrorMessage = messages.get("companyNameErrorMessage");
				loginForm.recordError(companyNameField, companyNameErrorMessage);
			}
		} else {
			if (StringUtils.isBlank(lastName)) {
                secondNameErrorMessage = messages.get("secondNameErrorMessage");
                loginForm.recordError(lastNameField, secondNameErrorMessage);
			}
			if (StringUtils.isBlank(firstName)) {
                firstNameErrorMessage =   messages.get("firstNameErrorMessage");
				loginForm.recordError(firstNameField, firstNameErrorMessage);
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
		List<Contact> users = new ArrayList<Contact>();
		if(iscompany) {
			users= authenticationService.authenticateCompany(companyName, email, password);
		} else {
			users = authenticationService.authenticate(firstName, lastName, email, password);
		}
		
		if (users.isEmpty()) {
            emailNameErrorMessage = messages.get("emailNameErrorMessage");
            passwordNameErrorMessage = messages.get("passwordNameErrorMessage");
            companyNameErrorMessage = messages.get("companyNameErrorMessage");
            secondNameErrorMessage =  messages.get("secondNameErrorMessage");
            firstNameErrorMessage =  messages.get("firstNameErrorMessage");
            loginForm.recordError("Login unsucessful! Invalid login name or password");
			return this;
		} else if (users.size() == 1) {
			authenticationService.storeCurrentUser(users.get(0));
			URL prevPage = cookieService.popPreviousPageURL();
			return (prevPage != null) ? prevPage : index;
		} else {
			
			Set<College> colleges = new HashSet<College>();
			Set<College> collegesWithDuplicates = new HashSet<College>();;
			
			// if in one college we have two or more contacts with identical login details
			// (we show the error on the login screen)
			for (Contact user : users) {
				if ( !colleges.add(user.getCollege()) ) {
				    // this college has already been added to the list, therefore there must be two or more duplicate contacts
				    // we cannot determine which one is correct, so don't let either of them in
					collegesWithDuplicates.add(user.getCollege());
				}
			}
			
			colleges.removeAll(collegesWithDuplicates);
			
			if (colleges.size() < 1 ) {
				loginForm.recordError("You are unable to log into this site with this set of credentials. Please contact the college and let them know that there are two contacts with identical login details. If they merge those contacts, the problem will be resolved.");
				return this;
			} 
		
			selectCollege.setTheUsers(users, collegesWithDuplicates);
			return selectCollege;
		}
	}
	
	private Object forgotPassword() {

		List<Contact> users = new ArrayList<Contact>();
		if(iscompany) {
			users= authenticationService.findCompanyForPasswordRecovery(companyName, email);
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
			
			Set<College> colleges = new HashSet<College>();
			Set<College> collegesWithDuplicates = new HashSet<College>();;
			
			// if in one college we have two or more contacts with identical login details
			// (we show the error on the login screen)
			for (Contact user : users) {
				if ( !colleges.add(user.getCollege()) ) {
				    // this college has already been added to the list, therefore there must be two or more duplicate contacts
				    // we cannot determine which one is correct, so don't let either of them in
					collegesWithDuplicates.add(user.getCollege());
				} 
			}
			
			colleges.removeAll(collegesWithDuplicates);
			
			if (colleges.size() < 1 ) {
				loginForm.recordError("You are unable to log into this site with this set of credentials. Please contact the college and let them know that there are two contacts with identical login details. If they merge those contacts, the problem will be resolved.");
				return this;
			} 
			
			selectCollege.setTheUsers(users, collegesWithDuplicates);
			return selectCollege;
		}
	}

    public String getUserDetailsStyle()
    {
        return  iscompany != null && iscompany ? "display: none;" : "display: block;";
    }

    public String getCompanyDetailsStyle()
    {
        return  iscompany != null && iscompany ? "display: block;" : "display: none;";
    }

    public String getUserDetailsClass()
    {
        return iscompany != null && iscompany ? "user-details collapse": "user-details ";
    }

    public String getCompanyDetailsClass()
    {
        return iscompany != null && iscompany ? "company-details ": "company-details collapse";
    }

    public String getCompanyNameErrorMessage() {
        return companyNameErrorMessage;
    }

    public String getFirstNameErrorMessage() {
        return firstNameErrorMessage;
    }

    public String getSecondNameErrorMessage() {
        return secondNameErrorMessage;
    }

    public String getEmailNameErrorMessage() {
        return emailNameErrorMessage;
    }

    public String getPasswordNameErrorMessage() {
        return passwordNameErrorMessage;
    }
}
