package ish.oncourse.services.mail;

import ish.oncourse.services.site.IWebSiteService;
import ish.persistence.CommonPreferenceController;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class MailService {

	private final static Logger LOGGER = Logger.getLogger(MailService.class);
	@Inject
	private CommonPreferenceController preferenceController;

	@Inject
	private IWebSiteService webSiteService;

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
		Properties props = System.getProperties();
		if (!props.containsKey("mail.smtp.host")) {
			LOGGER.error("SMPT host is not defined!");
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
			LOGGER.error("Exception on sending mail:" + e.getMessage());
			return false;
		}
		return true;
	}
}
