package ish.oncourse.portal.pages;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.services.PortalUtils;
import ish.oncourse.services.mail.EmailBuilder;
import ish.oncourse.services.mail.IMailService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.utils.SessionIdGenerator;
import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Calendar;

public class ForgotPassword {

    private static final Logger LOGGER = Logger.getLogger(ForgotPassword.class);

	/**
	 * Recover link live time 24 hours.
	 */
	private static final int RECOVER_LINK_TTL = 24;
	

	@Persist
	private Contact user;

	@Inject
	private IMailService mailService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Request request;

    @InjectPage
    private Login login;

	public void setUser(Contact user) {
		this.user = user;
	}


	public Object onActivate()
	{
		if (user == null)
			return Login.class.getSimpleName();
		return null;
	}

	@SetupRender
	void setupRender() {
		// creating expire link
		ObjectContext ctx = cayenneService.newContext();
		Contact c = ctx.localObject(user);

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.HOUR, RECOVER_LINK_TTL);
		c.setPasswordRecoverExpire(calendar.getTime());

		SessionIdGenerator idGenerator = new SessionIdGenerator();
		String passwordRecoverKey = idGenerator.generateSessionId().substring(0, 16);
		c.setPasswordRecoveryKey(passwordRecoverKey);

		ctx.commitChanges();

		String recoveryLink = String.format("https://%s/passwordrecovery/%s", request.getServerName() + request.getContextPath(), passwordRecoverKey);

		EmailBuilder email = new EmailBuilder();
		
		email.setFromEmail(PortalUtils.FROM_EMAIL);
		email.setSubject("Password reset.");
		
		StringBuilder textBody = new StringBuilder();
		textBody.append(String.format("Dear %s, <br/><br/>", user.getFullName()));
		textBody.append("To reset your SkillsOnCourse password, simply click the link below. That will take you to a web page where you can create a new password.<br/>");
		textBody.append("Please note that the link will expire 24 hours after this email was sent.<br/><br/>");
		textBody.append(String.format("<a href=\"%s\">%s</a><br/><br/>", recoveryLink, recoveryLink));
		textBody.append("If you weren't trying to reset your password, don't worry - your account is still secure and no one has been given access to it. " +
			"Most likely, someone just mistyped their email address while trying to reset their own password.");
		
		email.setBody(textBody.toString());
		email.setToEmails(c.getEmailAddress());
		
		mailService.sendEmail(email, true);
	}

    /**
     * The method has been introduced to redirect users to login page when session expired
     */
    public Object onException(Throwable cause){
        if (user == null) {
            LOGGER.warn("Persist properties have been cleared.", cause);
        } else {
            throw new IllegalArgumentException(cause);
        }
        return login;
    }
}
