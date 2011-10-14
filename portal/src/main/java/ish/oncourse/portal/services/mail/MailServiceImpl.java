package ish.oncourse.portal.services.mail;

import ish.oncourse.services.jndi.ILookupService;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class MailServiceImpl implements IMailService {

	private static final Logger logger = Logger.getLogger(MailServiceImpl.class);

	@Inject
	private ILookupService lookupService;

	@Override
	public boolean sendEmail(EmailBuilder email, boolean asynchronous) {

		final Message message;

		try {
			Session session = (Session) lookupService.lookup("mail/Session");
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
}
