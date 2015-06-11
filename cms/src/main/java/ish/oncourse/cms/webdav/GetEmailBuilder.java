package ish.oncourse.cms.webdav;

import ish.oncourse.services.mail.EmailBuilder;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class GetEmailBuilder {
	private ErrorEmailTemplate errorEmailTemplate;
	private String to;
	private String[] bodyParameters;

	public EmailBuilder get()
	{
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setFromEmail(errorEmailTemplate.getFrom());
		emailBuilder.setToEmails(to);
		emailBuilder.setSubject(errorEmailTemplate.getSubject());
		//we should replace all line breaks to <p> because our MailService sends mail as html
		emailBuilder.setBody(String.format(errorEmailTemplate.getBodyTemplate(), bodyParameters).replace("\n", "<p>"));
		return emailBuilder;
	}

	public static GetEmailBuilder valueOf(ErrorEmailTemplate errorEmailTemplate, String to, String... bodyParameter) {
		GetEmailBuilder getEmailBuilder = new GetEmailBuilder();
		getEmailBuilder.errorEmailTemplate =errorEmailTemplate;
		getEmailBuilder.to = to;
		getEmailBuilder.bodyParameters = bodyParameter;
		return getEmailBuilder;
	}

}
