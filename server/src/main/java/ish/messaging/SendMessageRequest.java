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
package ish.messaging;

import ish.oncourse.server.cayenne.SystemUser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SendMessageRequest implements Serializable {

	private static final String ID_PROPERTY = "id";

	private String emailFrom;
	private String emailSubject;
	private String emailBody;
	private String emailHtmlBody;
	private String smsText;
	private String postDescription;

	private Map<Long, SendingPreference> recipients = new HashMap<>();
	private Long createdById;

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public String getEmailHtmlBody() {
		return emailHtmlBody;
	}

	public void setEmailHtmlBody(String emailHtmlBody) {
		this.emailHtmlBody = emailHtmlBody;
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public String getPostDescription() {
		return postDescription;
	}

	public void setPostDescription(String postDescription) {
		this.postDescription = postDescription;
	}

	public Map<Long, SendingPreference> getRecipients() {
		return recipients;
	}

	public void addRecipient(IContact recipient, SendingPreference sendingPreference) {
		this.recipients.put((Long) recipient.getObjectId().getIdSnapshot().get(ID_PROPERTY), sendingPreference);
	}

	public Long getCreatedById() {
		return createdById;
	}

	public void setCreatedBy(SystemUser createdById) {
		this.createdById = (Long) createdById.getObjectId().getIdSnapshot().get(ID_PROPERTY);
	}

	public static class SendingPreference implements Serializable {
		private boolean sendEmail;
		private boolean sendSms;
		private boolean sendPost;

		public SendingPreference() {}

		public SendingPreference(boolean sendEmail, boolean sendSms, boolean sendPost) {
			this.sendEmail = sendEmail;
			this.sendSms = sendSms;
			this.sendPost = sendPost;
		}

		public void setSendEmail(boolean sendEmail) {
			this.sendEmail = sendEmail;
		}

		public boolean isSendEmail() {
			return sendEmail;
		}

		public void setSendSms(boolean sendSms) {
			this.sendSms = sendSms;
		}

		public boolean isSendSms() {
			return sendSms;
		}

		public void setSendPost(boolean sendPost) {
			this.sendPost = sendPost;
		}

		public boolean isSendPost() {
			return sendPost;
		}
	}
}
