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

import ish.common.types.MessageStatus;
import ish.common.types.MessageType;
import ish.oncourse.server.cayenne.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Send a message through the internal onCourse message sending mechanism.
 * Use this mechanism in preference to SMTPMessage when you:
 * * Don't have attachments
 * * Want to send email to contacts in the onCourse database
 * * Need to keep a record of sent mail within onCourse
 *
 */
public class EmailMessage {

	private static final String SUBJECT = "subject";
	private static final String TO = "to";
	private TemplateService templateService;

	private EmailTemplate template;
	private String fromAddress;
	private String creatorKey;
	private Map<String, Object> bindings = new HashMap<>();
	private Map<Contact, String> recipients = new HashMap<>();
	private SystemUser createdByUser;
	private boolean batchIsOver = false;
	private Integer emailBatch = null;

	/**
	 * Create a new message.
	 *
	 * @param template
	 * @param templateService
	 */
	public EmailMessage(EmailTemplate template, TemplateService templateService, boolean batchIsOver) {
		this.templateService = templateService;
		this.template = template;
		this.batchIsOver = batchIsOver;
	}

	public EmailMessage(EmailTemplate template, TemplateService templateService, Integer emailBatch) {
		this.templateService = templateService;
		this.template = template;
		this.emailBatch = emailBatch;
	}

	public EmailTemplate getTemplate() {
		return template;
	}

	public EmailMessage bind(Map<String, Object> bindings) {
		this.bindings.putAll(bindings);
		return this;
	}

	/**
	 * The from address should be a regular well formed email address.
	 * It will be very common to use <pre>email.from(Preferences.get("email.from"))</pre>
	 * to use the system default.
	 *
	 * @param email
	 */
	public void from(String email) {
		this.fromAddress = email;
	}

	/**
	 *
	 * @param creatorKey
	 */
	public void creatorKey(String creatorKey){
		this.creatorKey = creatorKey;
	}

	/**
	 * The recipient of this email must be a contact record.
	 * The email address is found from the contact record.
	 *
	 * @param recipient
	 * @return
	 */
	public EmailMessage to(Contact recipient) {
		this.recipients.put(recipient, recipient.getEmail());
		return this;
	}

	/**
	 * Address this message to a contact, but override the destination email address.
	 * The email address of the contact record is not updated with this new value.
	 *
	 * @param recipient
	 * @param email
	 * @return
	 */
	public EmailMessage to(Contact recipient, String email) {
		this.recipients.put(recipient, email);
		return this;
	}

	public void createdBy(SystemUser user) {
		this.createdByUser = user;
	}

	/**
	 * Queue the message for sending. This mechanism has been optimised for sending to large
	 * lists of contacts (over 100,000).
	 *
	 * @return
	 */
	public boolean send() {
		var context = template.getObjectContext();


		var messages = createMessages(template, bindings, recipients);

		if (!messages.isEmpty()) {
			context.commitChanges();

			return true;
		}

		return false;
	}

	private Message createMessage(EmailTemplate template, Map<String, Object> bindings) {
		var context = template.getObjectContext();

		var message = context.newObject(Message.class);

		message.setCreatedBy(createdByUser);
		message.setCreatorKey(creatorKey);
		String subject = templateService.renderSubject(template, bindings);
		bindings.put(SUBJECT, subject);

		message.setEmailSubject(subject);
		message.setEmailBody(templateService.renderPlain(template, bindings));
		message.setEmailHtmlBody(templateService.renderHtml(template, bindings));

		message.setEmailFrom(fromAddress);

		return message;
	}

	private List<Message> createMessages(EmailTemplate template, Map<String, Object> bindings, Map<Contact, String> recipients) {
		var context = template.getObjectContext();;

		List<Message> messages = new ArrayList<>();

		MessageStatus status;
		if(emailBatch != null && emailBatch < recipients.size() || batchIsOver)
			status = MessageStatus.FAILED;
		else
			status = MessageStatus.QUEUED;

		for (var entry : recipients.entrySet()) {
			if (StringUtils.trimToNull(entry.getValue()) != null) {
				var localContact = context.localObject(entry.getKey());

				bindings.put(TO, localContact);
				var message = createMessage(template, bindings);

				message.setNumberOfAttempts(0);

				message.setStatus(status);
				message.setType(MessageType.EMAIL);
				message.setContact(localContact);
				message.setDestinationAddress(entry.getValue());

				messages.add(message);
			}
		}

		return messages;
	}
}
