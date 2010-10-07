package ish.oncourse.cms.pages;

import ish.oncourse.services.security.AuthenticationStatus;
import ish.oncourse.services.security.IAuthenticationService;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * CMS login page.
 */
public class Login {

	@Persist
	@Property
	private String email;

	@Property
	private String password;

	@Component
	private Form loginForm;

	@InjectComponent("email")
	private TextField emailField;

	@InjectComponent("password")
	private PasswordField passwordField;

	@Inject
	private IAuthenticationService authenticationService;

	Object onSuccess() throws IOException {

		// TODO: What if there is a user logged in?

		if (StringUtils.isBlank(email)) {
			loginForm.recordError(emailField, "Please enter your login name");
		}
		if (StringUtils.isBlank(password)) {
			loginForm.recordError(passwordField, "Please enter your password");
		}

		if (!loginForm.getHasErrors()) {
			AuthenticationStatus status = authenticationService.authenticate(
					email, password);

			if (status == AuthenticationStatus.NO_MATCHING_USER) {
				loginForm
						.recordError("Login unsucessful! Invalid login name or password");
			} else if (status == AuthenticationStatus.MORE_THAN_ONE_USER) {
				loginForm
						.recordError("Login unsuccessful! There is a problem with your account, please contact the college for support (MU)");
			} else if (status != AuthenticationStatus.SUCCESS) {
				loginForm.recordError("Login unsuccessful! " + status.name());
			}
		}
		
		return (loginForm.getHasErrors()) ? this : Index.class;
	}

}
