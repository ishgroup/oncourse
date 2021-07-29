package ish.oncourse.services.mail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class EmailBuilder {

	protected String toEmails;
	protected String ccs;
	protected String bccs;
	protected String fromEmail;
	protected String fromName;
	protected String subject;
	protected StringBuilder body;
	
	private Logger logger = LogManager.getLogger();

	public EmailBuilder() {
		this.body = new StringBuilder();
	}

	public Message toMessage(Session session) throws MessagingException {

		// validate
		if (toEmails == null) {
			throw new IllegalArgumentException("No 'To:' address provided");
		}

		if (fromEmail == null) {
			throw new IllegalArgumentException("No 'From:' address provided");
		}

		if (subject == null) {
			throw new IllegalArgumentException("No email subject provided");
		}

		// build message
		Message message = new MimeMessage(session);

		updateFrom(message);
		updateTo(message);
		updateCC(message);
		updateBCC(message);

		message.setSubject(subject);
		message.setContent(body.toString(), "text/html");
		message.setHeader("X-Mailer", "IshRecoverPassword");
		message.setSentDate(new Date());

		return message;
	}

	private void updateFrom(Message message) throws MessagingException {
		if (fromEmail != null) {

			InternetAddress address = new InternetAddress(fromEmail);
			if (fromName != null) {
				try {
					address.setPersonal(fromName);
				} catch (UnsupportedEncodingException e) {
					// ignore
					logger.catching(e);
				}
			}
			message.setFrom(address);
		}
	}

	private void updateTo(Message message) throws MessagingException {
		if (toEmails != null) {
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmails, false));
		}
	}

	private void updateCC(Message message) throws MessagingException {
		if (ccs != null) {
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccs, false));
		}
	}

	private void updateBCC(Message message) throws MessagingException {
		if (bccs != null) {
			message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccs, false));
		}
	}

	/**
	 * Appends text String to the internal body text buffer.
	 */
	public EmailBuilder appendBodyText(String text) {
		body.append(text);
		return this;
	}

	public void setBccs(String bccs) {
		this.bccs = bccs;
	}

	/**
	 * Initializes email body with the new contents. Any existing contents are
	 * discarded.
	 */
	public void setBody(String body) {
		this.body = body != null ? new StringBuilder(body) : new StringBuilder();
	}

	public void setCcs(String ccs) {
		this.ccs = ccs;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Sets a comma-separated list of email addresses. Any existing addresses
	 * are discarded.
	 */
	public void setToEmails(String toEmails) {
		this.toEmails = toEmails;
	}
}
