package ish.oncourse.services.mail;

import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.commons.validator.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class MailService implements IMailService {

	public static final String SYSTEM_PROPERTY_SMTP_HOST = "mail.smtp.host";
	private final static Logger logger = LogManager.getLogger();
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
				logger.error("The email.from preference is not configured for the college"
						+ webSiteService.getCurrentCollege().getName());
				return false;
			}

		}
		if (to == null || !validator.isValid(to)) {
			logger.error("Bad recipient address");
			return false;
		}
		Properties props = System.getProperties();
		if (!props.containsKey("mail.smtp.host")) {
			logger.error("SMPT host is not defined!");
		}
		Session session = Session.getDefaultInstance(props, null);
		// -- Create a new message --
		Message msg = new MimeMessage(session);

		try {
			// -- Set the FROM and TO fields --
			msg.setFrom(new InternetAddress(from));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
			// -- Set the subject and body text --
			msg.setSubject(subject);
			msg.setText(body);
			msg.setSentDate(new Date());
			// -- Send the message --
			Transport.send(msg);
		} catch (Exception e) {
			logger.catching(e);
			return false;
		}
		return true;
	}

	@Override
	public boolean sendEmail(EmailBuilder email, boolean asynchronous) {

		final Message message;

		try {
			Session session = getSession();
			message = email.toMessage(session);
		} catch (MessagingException e) {
			logger.warn("Failed to prepare message", e);
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
			logger.debug("Email sent successfully");
			return true;
		} catch (MessagingException e) {
			logger.warn("Error sending email.", e);
			return false;
		}
	}

	private Session getSession() {
		Properties props = System.getProperties();
		if (!props.containsKey(SYSTEM_PROPERTY_SMTP_HOST)) {
			logger.error("SMTP host is not defined!");
		}
		return Session.getDefaultInstance(props, null);
	}

}
