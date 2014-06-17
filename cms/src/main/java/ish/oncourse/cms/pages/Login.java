package ish.oncourse.cms.pages;


import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.services.access.AuthenticationStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

import java.io.IOException;

import static ish.oncourse.cms.services.Constants.HOME_PAGE;
import static ish.oncourse.cms.services.Constants.LOGIN_PAGE;


/**
 * CMS login page.
 */
public class Login {

	@Persist
	@Property
	private String logEmail;

	@Property
	private String password;

	@Component
	private Form loginForm;
	
	@Inject
	private Request request;

    @Inject
    private Response response;

	@InjectComponent("logEmail")
	private TextField emailField;

	@InjectComponent("password")
	private PasswordField passwordField;

	@Inject
	private IAuthenticationService authenticationService;

	void setupRender(){
		//perform logout to cleanup the session before the new login
		authenticationService.logout();
	}

	public void onSuccess() throws IOException {

		if (StringUtils.isBlank(logEmail)) {
			loginForm.recordError(emailField, "Please enter your login name");
		}
		if (StringUtils.isBlank(password)) {
			loginForm.recordError(passwordField, "Please enter your password");
		}

		if (!loginForm.getHasErrors()) {
			AuthenticationStatus status = authenticationService.authenticate(
					logEmail, password);

			if (status == AuthenticationStatus.NO_MATCHING_USER) {
				loginForm
						.recordError("Login unsuccessful. Invalid login name or password.");
			} else if (status == AuthenticationStatus.MORE_THAN_ONE_USER) {
				loginForm
						.recordError("Login unsuccessful. There are two users with the same login details. Please contact the college for help.");
			} else if (status != AuthenticationStatus.SUCCESS) {
				loginForm.recordError("Login unsuccessful. " + status.name());
			}
		}

        /**
         * The line
         * return (loginForm.getHasErrors()) ? this : new URL("http://" + request.getServerName());
         * was replaced to current as workaround for task
         */
         if (loginForm.getHasErrors()) {
            response.sendRedirect(LOGIN_PAGE);
         }
         else {
             response.sendRedirect(HOME_PAGE);
         }

    }
}
