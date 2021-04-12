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

package ish.oncourse.server.scripting.api

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.messaging.DocumentParam
import ish.util.MessageUtils
import org.apache.cayenne.PersistentObject

/**
 * Email sending API. You can render email content from templates or a text string. If the recipient is a contact object
 * and there is no attachment, the message is stored inside the onCourse database. If the recipient is an email address
 * or there is an attachment, then the message is sent directly without storage.
 *
 * Simple usage example:
 * ```
 * email {
 *     template "Enrolment Confirmation"
 *     bindings enrolment: e
 *     to c
 * }
 * ```
 * The above example will render the template with the name "Enrolment Confirmation" and pass "enrolment" to that template
 * with the value of the Enrolment e in the script. It will send that email to the contact c.
 *
 * You can optionally also pass a from address if you don't want to use the default email.from preference. And you can override
 * the email address associated with a certain contact or send to a list of contacts.
 *
 * ```
 * email {
 *     template "Enrolment Confirmation"
 *     from "college@example.com"
 *     to c1, c2 >> "anotheremail@example.com"
 *     bindings enrolment: e
 * }
 * ```
 * The only lines which are required are "template", "bindings" and "to"
 *
 *
 * You can send emails to an email address (instead of a contact record) and attach a file, without storing this data inside onCourse.
 *
 * Usage example:
 * ```
 *  email {
 *      from "admin@example.com"
 *      to "recipient@example.com"
 *      cc "ccrecipient@example.com"
 *      bcc "bccrecipient@example.com"
 *      subject "test email"
 *      content "test email content"
 *      attachment "accounts.csv", "text/csv", account_csv_data
 *  }
 * ```
 * The only lines which are required are "to", "subject" and "content" or "to", "template" and "bindings"
 *
 *
 * Email collision functionality. Allows to skip sending emails automatically if contact have already received email with specified key.
 *
 * Usage example:
 * ```
 * email {
 *     template "Enrolment Confirmation"
 *     bindings enrolment: e
 *     to c
 *     key "ABC", c
 *     keyCollision "drop"
 * }
 * ```
 */
@Deprecated
@CompileStatic
class EmailSpec {

	String fromAddress
	String templateName
	String creatorKey
	KeyCollision keyCollision = KeyCollision.accept
	Map<Contact, String> recipients = [:]
	Map<String, Object> bindings = [:]
	SystemUser createdBy

	String fromName
	String subject
	String content
	List<String> toList = []
	List<String> ccList = []
	List<String> bccList = []
	String multipartType = SMTPMessage.DEFAULT_MULTIPART_TYPE
	List<DocumentParam> attachments = []


	/**
	 * Set email template to be used for rendering email body.
	 * Could be used for Email and SMTP email sending
	 *
	 * @param templateName name of the email template
     */
	void template(String templateName) {
		this.templateName = templateName
	}

	/**
	 * Set "from" address for the email. Defaults to "email.from" preference if not set.
	 *
	 * @param email email from address
     */
	void from(String email) {
		this.fromAddress = email
	}

	/**
	 * Specify contacts which will receive email. By default email specified in contact record will be used,
	 * however you can override the default email by using ">>" operator, e.g. contact1 >> "anotheremail@example.com"
	 *
	 * @param recipients contact records who will receive the email
     */
	@CompileStatic(TypeCheckingMode.SKIP)
	void to(Contact... recipients) {
		recipients.each { r ->
			String email
			try {
				email = r.replacementEmail ?: r.email
			} catch (MissingPropertyException ignored) {
				email = r.email
			}
			this.recipients[r] = email
		}
	}

	/**
	 * Set bindings necessary for rendering of the specified email template.
	 *
	 * @param bindings binding mappings required for rendering email template
     */
	void bindings(Map<String, Object> bindings) {
		this.bindings = bindings
	}

	/**
	 * Specify SystemUser who will be the linked to the resulting Message record in onCourse as its creator.
	 *
	 * @param user user who created the email
     */
	void createdBy(SystemUser user) {
		this.createdBy = user
	}

	/**
	 * Set "from" address for the email and name of the sender.
	 *
	 * @param email email from address
	 * @param name name of the sender
	 */
	void from(String email, String name) {
		this.fromAddress = email
		this.fromName = name
	}

	/**
	 * Attach a string key to this message and link it to an object. This reference can be used to ensure duplicates
	 * aren't sent for the same event.
	 *
	 * @param key a string key which identifies the script or event which creates this message
	 * @param object key is attached to a specific object (for example an enrolment or student)
	 */
	void key(String key, PersistentObject... object){
		this.creatorKey = MessageUtils.generateCreatorKey(key, object)
	}

	/**
	 * Defines a rule to prevent duplicates being sent.
	 *
	 * @param collision check type. Can be 'accept' (this is the default and means the key is ignored), 'drop' (which will silently drop the message) and 'error' (which will drop the message and log an error to the audit log).
	 */
	void keyCollision(String collision){
		this.keyCollision = KeyCollision.valueOf(collision)
	}

	/**
	 * Set recipients for the email. Using this method means that the message is not stored inside onCourse.
	 *
	 * @param recipients email addresses of the email recipients
	 */
	void to(String... recipients) {
		this.toList.addAll(recipients)
	}

	/**
	 * Set CC recipients for the email.  Using this method means that the message is not stored inside onCourse.
	 *
	 * @param recipients email addresses of the CC recipients
	 */
	void cc(String... recipients) {
		this.ccList = recipients.toList()
	}

	/**
	 * Set BCC recipients for the email. Using this method means that the message is not stored inside onCourse.
	 *
	 * @param recipients email address of the BCC recipients
	 */
	void bcc(String... recipients) {
		this.bccList = recipients.toList()
	}

	/**
	 * Set email subject.
	 *
	 * @param subject email subject
	 */
	void subject(String subject) {
		this.subject = subject
	}

	/**
	 * Set plain text content of the email.
	 * Could be used only for SMTP email sending
	 *
	 * @param content plain text content of the email
	 */
	void content(String content) {
		this.content = content
	}

	/**
	 * Specify multipart message type (default is "mixed"). Using this method means that the message is not stored inside onCourse.
	 *
	 * @param multipartType multipart message type
	 */
	void multipartType(String multipartType) {
		this.multipartType = multipartType
	}

	/**
	 * Add attachment to the email. Using this method means that the message is not stored inside onCourse.
	 *
	 * @param contentType MIME type of the attachment
	 * @param content MIME type of the attachment
	 */
	void attachment(String contentType, Object content) {
		this.attachments << DocumentParam.valueOf(null, contentType, content)
	}

	/**
	 * Add attachment to the email. Using this method means that the message is not stored inside onCourse.
	 *
	 * @param fileName attached file name which will appear in the email
	 * @param contentType MIME type of the attachment
	 * @param content attachment object
	 */
	void attachment(String fileName, String contentType, Object content) {
		this.attachments << DocumentParam.valueOf(fileName, contentType, content)
	}

	/**
	 * Add attachment to the email. Using this method means that the message is not stored inside onCourse.
	 *
	 * @param attachment attachment properties map, e.g. [fileName: 'example.txt', type: 'text/plain', content: 'test text']
	 */
	void attachment(Map<String, Object> attachment) {
		this.attachments << DocumentParam.valueOf((String) attachment.fileName, (String) attachment.type, attachment.content)
	}
}
