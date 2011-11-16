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
		
		StringBuilder textBody = new StringBuilder();
		textBody.append(String.format("Dear %s %s, <br/><br/>", user.getGivenName(), user.getFamilyName()));
		textBody.append("To reset your SkillsOnCourse password, simply click the link below. That will take you to a web page where you can create a new password.<br/>");
		textBody.append("Please note that the link will expire 24 hours after this email was sent.<br/><br/>");
		textBody.append(String.format("<a href=\"%s\">%s</a>.<br/><br/>", recoveryLink, recoveryLink));
		textBody.append("If you weren't trying to reset your password, don't worry Ñ your account is still secure and no one has been given access to it. " +
				"Most likely, someone just mistyped their email address while trying to reset their own password.");
		
		email.setBody(textBody.toString());
		email.setToEmails(c.getEmailAddress());
		
		mailService.sendEmail(email, true);
	}
}
