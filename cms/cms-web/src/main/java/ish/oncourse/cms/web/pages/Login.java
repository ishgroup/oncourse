package ish.oncourse.cms.web.pages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.cms.services.security.AutenticationStatus;
import ish.oncourse.cms.services.security.IAuthenticationService;

/**
 * CMS login page.
 */
public class Login {

	@Property
	@Persist
	private String email;

	@Property
	private String password;

	@Component
	private Form loginForm;

	@InjectPage
	private Index defaultReturnPage;

	@Inject
	private IAuthenticationService authenticationService;

	Object onSuccess() {
		AutenticationStatus status = authenticationService.authenticate(email,
				password);

		if (status == AutenticationStatus.SUCCESS) {
			return defaultReturnPage;
		}

		loginForm.recordError(status.name());

		return this;
	}

}
