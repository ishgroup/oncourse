package ish.oncourse.cms.webdav;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ErrorEmailTemplate {
	private String from;
	private String subject;
	private String bodyTemplate;

	public String getFrom() {
		return from;
	}

	public String getSubject() {
		return subject;
	}

	public String getBodyTemplate() {
		return bodyTemplate;
	}

	public static ErrorEmailTemplate valueOf(String from, String subject, String bodyTemplate) {
		ErrorEmailTemplate errorEmailTemplate = new ErrorEmailTemplate();
		errorEmailTemplate.from = from;
		errorEmailTemplate.subject = subject;
		errorEmailTemplate.bodyTemplate = bodyTemplate;
		return errorEmailTemplate;
	}

}
