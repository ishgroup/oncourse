package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.IOException;
import java.util.Date;

public class PasswordRecovery {

	@Property
	private String password;

	@Property
	private String confirmpassword;

	@Component
	private Form recoveryForm;

	@InjectComponent("password")
	private PasswordField passwordField;

	@InjectComponent("confirmpassword")
	private PasswordField confirmPasswordField;

	@Inject
	private IAuthenticationService authService;

	@Inject
	private ICayenneService cayenneService;

	@Property
	@Persist
	private Contact contact;
	
	@InjectPage
    private Index index;
	
	void onActivate(String recoveryKey) {
		Contact user = authService.findByPasswordRecoveryKey(recoveryKey);

		if (user != null) {
			ObjectContext newContext = cayenneService.newContext();

			this.contact = (Contact) newContext.localObject(user.getObjectId(), null);

			Date today = new Date();

			if (today.compareTo(contact.getPasswordRecoverExpire()) > 0) {
				contact.setPasswordRecoverExpire(null);
				contact.setPasswordRecoveryKey(null);
				newContext.commitChanges();
			}
		}
	}

	Object onSuccess() throws IOException {

		if (StringUtils.isBlank(password)) {
			recoveryForm.recordError(passwordField, "Please enter your password");
		}
        else if (StringUtils.isBlank(confirmpassword)) {
			recoveryForm.recordError(confirmPasswordField, "Please, confirm your password");
		}
        else if (!password.equals(confirmpassword)) {
			recoveryForm.recordError(confirmPasswordField, "Password does not match the confirm password.");
		}
		
		if (recoveryForm.getHasErrors()) {
			return this;
		}

		contact.setNewPassword(password);
		contact.getObjectContext().commitChanges();
		
		authService.storeCurrentUser(contact);

		return getTimetablePage();
	}
	
	private Object getTimetablePage() {
		return index;
	}

	public boolean getIsExpired() {
		return this.contact == null || this.contact.getPasswordRecoverExpire() == null;
	}
}
