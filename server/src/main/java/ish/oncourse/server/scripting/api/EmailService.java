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

import com.google.inject.Inject;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import ish.oncourse.server.messaging.MessageService;

import javax.mail.MessagingException;

public class EmailService {

	private MessageService messageService;

	@Inject
	public EmailService(MessageService messageService) {
		this.messageService = messageService;
	}

	@Deprecated
	public EmailMessage create(String templateName) {
		return messageService.createEmailMessage(templateName);
	}


	@Deprecated
	public SMTPMessage create() {
		return messageService.createSmtpMessage();
	}

	public void email(@DelegatesTo(EmailSpec.class) Closure cl) throws MessagingException {
		var emailSpec = new EmailSpec();
		var build = cl.rehydrate(emailSpec, cl, this);
		build.setResolveStrategy(Closure.DELEGATE_FIRST);
		build.call();

        createEmail(emailSpec);
	}

	public void createEmail(EmailSpec spec) throws MessagingException {
		messageService.sendEmail(spec);
    }

	@Deprecated
	public void smtp(@DelegatesTo(SmtpSpec.class) Closure cl) throws MessagingException {
		var smtpSpec = new SmtpSpec();
		var build = cl.rehydrate(smtpSpec, cl, this);
		build.setResolveStrategy(Closure.DELEGATE_FIRST);
		build.call();

		messageService.sendSmtp(smtpSpec);
	}
}
