package ish.oncourse.services.mail;

import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;

public class MailService implements IMailService {

	public static final String SYSTEM_PROPERTY_SMTP_HOST = "mail.smtp.host";
	private final static Logger LOGGER = Logger.getLogger(MailService.class);
	@Inject
	private PreferenceController preferenceController;
	@Inject
	private IWebSiteService webSiteService;

	@Override
	public boolean sendMail(String from, String to, String subject, String body) {

		EmailValidator validator = EmailValidator.getInstance();

		if (from == null) {
			from = preferenceController.getEmailFromAddress();

			if (from == null || !validator.isValid(from)) {
				LOGGER.error("The email.from preference is not configured for the college"
						+ webSiteService.getCurrentCollege().getName());
				return false;
			}

		}
		if (to == null || !validator.isValid(to)) {
			LOGGER.error("Bad recipient address");
			return false;
		}

		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setFromEmail(from);
		emailBuilder.setToEmails(to);
		emailBuilder.setSubject(subject);
		emailBuilder.setBody(body);
		// -- Send the message --
		return sendEmail(emailBuilder, false);
	}

	@Override
	public boolean sendEmail(EmailBuilder email, boolean asynchronous) {

		final Message message;

		try {
			Session session = getSession();
			message = email.toMessage(session);
		} catch (MessagingException e) {
			LOGGER.warn("Failed to prepare message", e);
			return false;
		}

		if (asynchronous) {
			Runnable r = new Runnable() {
				public void run() {
					doSend(message);
				}
			};

			Thread mailThread = new Thread(r, "email");
			mailThread.setDaemon(true);
			mailThread.start();
			return true;

		} else {
			return doSend(message);
		}
	}

	private boolean doSend(Message message) {
		try {
			Transport.send(message);
			LOGGER.debug("Email sent successfully");
			return true;
		} catch (MessagingException e) {
			LOGGER.warn("Error sending email.", e);
			return false;
		}
	}

	private Session getSession() {
		Properties props = System.getProperties();
		if (!props.containsKey(SYSTEM_PROPERTY_SMTP_HOST)) {
			LOGGER.error("SMPT host is not defined!");
		}
		return Session.getDefaultInstance(props, null);
	}

}
