package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.services.mail.EmailBuilder;
import ish.oncourse.portal.services.mail.IMailService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.utils.SessionIdGenerator;

import java.util.Calendar;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ForgotPassword {

	/**
	 * Recover link live time 24 hours.
	 */
	private static final int RECOVER_LINK_TTL = 24;
	
	/**
	 * Recover emails from address.
	 */
	private static final String FROM_EMAIL = "support@ish.com.au";

	@Persist
	private Contact user;

	@Inject
	private IMailService mailService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Request request;

	public void setUser(Contact user) {
		this.user = user;
	}

	@SetupRender
	void setupRender() {
		// creating expire link
		ObjectContext ctx = cayenneService.newContext();
		Contact c = (Contact) ctx.localObject(user.getObjectId(), null);

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.HOUR, RECOVER_LINK_TTL);
		c.setPasswordRecoverExpire(calendar.getTime());

		SessionIdGenerator idGenerator = new SessionIdGenerator();
		String passwordRecoverKey = idGenerator.generateSessionId().substring(0, 16);
		c.setPasswordRecoveryKey(passwordRecoverKey);

		ctx.commitChanges();

		String recoveryLink = String.format("https://%s/passwordrecovery/%s", request.getServerName() + request.getContextPath(), passwordRecoverKey);

		EmailBuilder email = new EmailBuilder();
		
		email.setFromEmail(FROM_EMAIL);
		email.setSubject("Password recovery.");
		email.setBody(String.format("To recover password please visit <a href=\"%s\">%s</a>.", recoveryLink, recoveryLink));
		email.setToEmails(c.getEmailAddress());

		mailService.sendEmail(email, true);
	}
}
