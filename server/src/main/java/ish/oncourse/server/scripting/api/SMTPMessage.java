/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.scripting.api;

import ish.oncourse.server.messaging.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This helper class makes it easy to send email messages. Note that this does not create messages inside the onCourse database, but rather sends email without keeping any record of the mail being sent.
 * You might implement a script like this to send an email with an attachment:
 *
 * ```
 *  smtp {
 *      from Preferences.get("email.from")
 *      to "recipient@example.com"
 *      cc "ccrecipient@example.com"
 *      bcc "bccrecipient@example.com"
 *      subject "test email"
 *      content "test email content"
 *      attachment "accounts.csv", "text/csv", account_csv_data
 *  }
 * ```
 * The only lines which are required are "to", "subject" and "content"
 *
 */
@Deprecated
public class SMTPMessage {

	private MailDeliveryService mailDeliveryService;

	private static final Logger logger = LogManager.getLogger();

	public static final String DEFAULT_MULTIPART_TYPE = "mixed";

	private String fromAddress;
	private String fromName;

	private String subject;
	private String content;
	private String multipartType = DEFAULT_MULTIPART_TYPE;
	private List<BodyPart> bodyParts = new ArrayList<>();

	private Set<String> toList = new HashSet<>();
	private Set<String> ccList = new HashSet<>();
	private Set<String> bccList = new HashSet<>();

	public void setMailDeliveryService(MailDeliveryService mailDeliveryService) {
		this.mailDeliveryService = mailDeliveryService;
	}

	/**
	 * @param address sender email address
	 *
	 * @return instance of SMTPMessage
	 */
	@Deprecated
	public SMTPMessage from(String address) {
		this.fromAddress = address;
		return this;
	}

	/**
	 * @param address sender's email address
	 * @param name sender name
	 *
	 * @return instance of SMTPMessage
	 */
	@Deprecated
	public SMTPMessage from(String address, String name) {
		this.fromAddress = address;
		this.fromName = name;

		return this;
	}

	/**
	 * @param fromName sender name
	 *
	 * @return instance of SMTPMessage
	 */
	@Deprecated
	public SMTPMessage fromName(String fromName) {
		this.fromName = fromName;
		return this;
	}

	/**
	 * @param recipient recipient's email address
	 *
	 * @return instance of SMTPMessage
	 */
	@Deprecated
	public SMTPMessage to(String recipient) {
		this.toList.add(recipient);
		return this;
	}

	/**
	 * @param cc recipient's email address
	 *
	 * @return instance of SMTPMessage
	 */
	@Deprecated
	public SMTPMessage cc(String cc) {
		this.ccList.add(cc);
		return this;
	}

	/**
	 * @param bcc recipient's email address.
	 *
	 * @return instance of SMTPMessage
	 */
	@Deprecated
	public SMTPMessage bcc(String bcc) {
		this.bccList.add(bcc);
		return this;
	}

	/**
	 * @param subject email subject
	 *
	 * @return instance of SMTPMessage
	 */
	@Deprecated
	public SMTPMessage subject(String subject) {
		this.subject = subject;
		return this;
	}

	/**
	 * @param content plain text content
	 *
	 * @return instance of SMTPMessage
	 */
	@Deprecated
	public SMTPMessage content(String content) {
		this.content = content;
		return this;
	}

	/**
	 * Specify multipart message type (default is "mixed").
	 *
	 * @param type multipart message type
	 *
	 * @return instance of SMTPMessage
	 */
	@Deprecated
	public SMTPMessage setMultipartType(String type) {
		this.multipartType = type;
		return this;
	}

	/**
	 * Add body part to multipart email.
	 *
	 * @param bodyPart body part to add
	 *
	 * @return instance of SMTPMessage
	 */
	@Deprecated
	public SMTPMessage addPart(BodyPart bodyPart) {
		this.bodyParts.add(bodyPart);
		return this;
	}

	/**
	 * Add body part to multipart email.
	 *
	 * @param fileName name of attached file
	 * @param contentType content MIME type
	 * @param content content
	 *
	 * @return body part
	 */
	@Deprecated
	public SMTPMessage addPart(String fileName, String contentType, Object content) throws MessagingException {
		BodyPart part = new MimeBodyPart();

		if (fileName != null)  {
			part.setFileName(fileName);
		}

		part.setContent(content, contentType);

		return addPart(part);
	}

	/**
	 * Send email. This sends the email immediately and sychronously. That is,
	 * the function does not return until the email is sent. Therefore this function isn't
	 * suitable for sending large numbers of emails (over several thousands) or for very large
	 * emails.
	 *
	 */
	@Deprecated
	public void send() {
		try {
			var getFrom = GetFrom.valueOf(fromAddress, fromName);
			var getEnvelopeFrom = GetEnvelopeFrom.valueOf(fromAddress);
			var getAddressesTO = GetAddresses.valueOf(toList);
			var getAddressesCC = GetAddresses.valueOf(ccList);
			var getAddressesBCC = GetAddresses.valueOf(bccList);
			var getSubject = GetSubject.valueOf(subject);
			var getContent = getContentFunction();

			var mailDeliveryParam = MailDeliveryParam.valueOf(getFrom, getEnvelopeFrom, getAddressesTO, getAddressesCC, getAddressesBCC, getSubject, getContent, GetAddresses.empty());

			mailDeliveryService.sendEmail(mailDeliveryParam);

		} catch (Exception e) {
			logger.catching(e);
			throw new RuntimeException("Message sending failed.", e);
		}
	}

	//after removing this GetContent constructor should be private
	@Deprecated
	private GetContent getContentFunction() throws MessagingException{
		List<BodyPart> parts = new ArrayList<>();

		if (StringUtils.trimToNull(content) != null) {
			BodyPart part = new MimeBodyPart();
			part.setText(content);
			parts.add(part);
		}

		parts.addAll(bodyParts);

		return new GetContent() {

			@Override
			public Multipart get() throws MessagingException {
				Multipart mp = new MimeMultipart(multipartType);

				for (var part : parts) {
					mp.addBodyPart(part);
				}

				return mp;
			}
		};
	}
}
