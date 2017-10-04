package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.ioc.Messages;
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

    @Inject
    private Messages messages;

	@Property
	private Contact contact;

	@Persist(value = "client")
	private String recoveryKey;

	
	@InjectPage
    private Index index;
	
	void onActivate(String recoveryKey) {
		this.recoveryKey = recoveryKey;
	}

	@SetupRender
	void setupRender() {
		Contact user = authService.findByPasswordRecoveryKey(recoveryKey);

		if (user != null) {
			ObjectContext newContext = cayenneService.newContext();

			this.contact = newContext.localObject(user);

			Date today = new Date();

			if (today.compareTo(contact.getPasswordRecoverExpire()) > 0) {
				contact.setPasswordRecoverExpire(null);
				contact.setPasswordRecoveryKey(null);
				newContext.commitChanges();
			}
		}
	}
	
	@OnEvent(value = EventConstants.VALIDATE, component = "recoveryForm")
	void onValidate() throws IOException {

		if (StringUtils.isBlank(password)) {
			recoveryForm.recordError(passwordField, messages.get("message-enterPassword"));
		}
		else if (StringUtils.isBlank(confirmpassword)) {
			recoveryForm.recordError(confirmPasswordField, messages.get("message-confirmPassowrd"));
		}
		else if (!password.equals(confirmpassword)) {
			recoveryForm.recordError(confirmPasswordField, messages.get("message-confirmPassowrdNotMatch"));
		}
	}
	
	Object onSuccess() throws IOException {
		Contact user = authService.findByPasswordRecoveryKey(recoveryKey);
		ObjectContext newContext = cayenneService.newContext();
		contact = newContext.localObject(user);
		
		contact.setNewPassword(password);
		newContext.commitChanges();
		authService.storeCurrentUser(contact);
		recoveryKey = null;
		return getTimetablePage();
	}
	
	private Object getTimetablePage() {
		return index;
	}

	public boolean getIsExpired() {
		return this.contact == null || this.contact.getPasswordRecoverExpire() == null;
	}
}
