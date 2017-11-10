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

import static ish.oncourse.configuration.Configuration.AppProperty.SMTP;

public class MailService implements IMailService {

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
		if (!props.containsKey(SMTP.getSystemProperty())) {
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
		return SendEmail.valueOf(email, asynchronous).send();
	}
}
