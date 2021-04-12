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

import ish.oncourse.server.messaging.DocumentParam

/**
 * SMTP email sending API. Allows to send arbitrary emails with attachments with variety of sending configuration options,
 * doesn't require recipients to have contact record in onCourse and doesn't create Message records.
 *
 * Usage example:
 * ```
 *  smtp {
 *      from "admin@example.com"
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
class SmtpSpec {

	String fromAddress
    String fromName
    String subject
    String content
    List<String> toList = []
    List<String> ccList = []
    List<String> bccList = []
    String multipartType = SMTPMessage.DEFAULT_MULTIPART_TYPE
    List<DocumentParam> attachments = []

	/**
	 * Set "from" address for the email. Defaults to "email.from" preference if not set.
	 *
	 * @param email email "from" address
	 */
	void from(String email) {
		this.fromAddress = email
	}

	/**
	 * Set "from" address for the email and name of the sender.
	 *
	 * @param email email "from" address
	 * @param name name of the sender
	 */
	void from(String email, String name) {
		this.fromAddress = email
		this.fromName = name
	}

	/**
	 * Set recipients for the email.
	 *
	 * @param recipients email addresses of the email recipients
     */
	void to(String... recipients) {
		this.toList = recipients.toList()
	}

	/**
	 * Set CC recipients for the email.
	 *
	 * @param recipients email addresses of the CC recipients
     */
	void cc(String... recipients) {
		this.ccList = recipients.toList()
	}

	/**
	 * Set BCC recipients for the email.
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
	 *
	 * @param content plain text content of the email
     */
	void content(String content) {
		this.content = content
	}

	/**
	 * Specify multipart message type (default is "mixed").
	 *
	 * @param multipartType multipart message type
	 */
	void multipartType(String multipartType) {
		this.multipartType = multipartType
	}

	/**
	 * Add attachment to the email.
	 *
	 * @param contentType MIME type of the attachment
	 * @param content MIME type of the attachment
     */
	void attachment(String contentType, Object content) {
		this.attachments << DocumentParam.valueOf(null ,contentType, content)
	}

	/**
	 * Add attachment to the email.
	 *
	 * @param fileName attached file name which will appear in the email
	 * @param contentType MIME type of the attachment
	 * @param content attachment object
     */
	void attachment(String fileName, String contentType, Object content) {
		this.attachments << DocumentParam.valueOf(fileName, contentType, content)
	}

	/**
	 * Add attachment to the email.
	 *
	 * @param attachment attachment properties map, e.g. [fileName: 'example.txt', type: 'text/plain', content: 'test text']
     */
	void attachment(Map<String, Object> attachment) {
		this.attachments << DocumentParam.valueOf((String) attachment.fileName, (String) attachment.type, attachment.content)
	}
}
