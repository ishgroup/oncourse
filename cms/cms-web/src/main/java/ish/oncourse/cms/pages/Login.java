package ish.oncourse.cms.pages;


import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;

import ish.oncourse.cms.services.security.AuthenticationStatus;
import ish.oncourse.cms.services.security.IAuthenticationService;


/**
 * CMS login page.
 */
public class Login {

	public static final String CMS_COOKIE_NAME = "cms";
	public static final int CMS_COOKIE_AGE = 3600;

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
	private Cookies cookies;

	@Inject
	private IAuthenticationService authenticationService;


	Object onSuccess() {

		// TODO: What if there is a user logged in?

		if (StringUtils.isBlank(email)) {
			loginForm.recordError(emailField, "Please enter your login name");
		}
		if (StringUtils.isBlank(password)) {
			loginForm.recordError(passwordField, "Please enter your password");
		}

		if (! loginForm.getHasErrors()) {
			AuthenticationStatus status = authenticationService.authenticate(
					email, password);

			if (status == AuthenticationStatus.NO_MATCHING_USER) {
				loginForm.recordError("Login unsucessful! Invalid login name or password");
			} else if (status == AuthenticationStatus.MORE_THAN_ONE_USER) {
				loginForm.recordError("Login unsuccessful! There is a problem with your account, please contact the college for support (MU)");
			} else if (status != AuthenticationStatus.SUCCESS) {
				loginForm.recordError("Login unsuccessful! " + status.name());
				cookies.writeCookieValue(CMS_COOKIE_NAME, CMS_COOKIE_NAME,
						CMS_COOKIE_AGE);
			}
		}

		return (loginForm.getHasErrors()) ? this : Index.class;
	}

}
