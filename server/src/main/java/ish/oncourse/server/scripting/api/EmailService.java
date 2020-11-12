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
import ish.messaging.MessageParameter;
import ish.messaging.MessageSendingResult;
import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.cayenne.EmailTemplate;
import ish.oncourse.server.cayenne.SystemUser;
import ish.oncourse.server.messaging.MailDeliveryParam;
import ish.oncourse.server.messaging.MailDeliveryService;
import ish.oncourse.server.scripting.ScriptParameters;
import ish.oncourse.server.services.AuditService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.apache.commons.lang3.StringUtils;

import javax.mail.MessagingException;
import java.util.Map;
import java.util.function.Function;

public class EmailService {

	private TemplateService templateService;
	private ICayenneService cayenneService;
	private PreferenceController preferenceController;
	private MailDeliveryService mailDeliveryService;
	private AuditService auditService;

	@Inject
	public EmailService(TemplateService templateService, ICayenneService cayenneService,
						PreferenceController preferenceController, MailDeliveryService mailDeliveryService,
						AuditService auditService) {
		this.templateService = templateService;
		this.cayenneService = cayenneService;
		this.preferenceController = preferenceController;
		this.mailDeliveryService = mailDeliveryService;
		this.auditService = auditService;
	}

	@Deprecated
	public EmailMessage create(String templateName) {
		var template = templateService.loadTemplate(templateName);

		if (template == null) {
			throw new IllegalArgumentException(String.format("No template with name '%s' found.", templateName));
		}

		ObjectContext context = cayenneService.getNewContext();

		var localTemplate = context.localObject(template);
		var message = new EmailMessage(localTemplate, templateService);

		message.from(preferenceController.getEmailFromAddress());

		return message;
	}


	@Deprecated
	public SMTPMessage create() {
		var message = new SMTPMessage();
		message.setMailDeliveryService(mailDeliveryService);
		// fill email from address with defaults from preferences
		message.from(preferenceController.getEmailFromAddress());

		return message;
	}

	public void email(@DelegatesTo(EmailSpec.class) Closure cl) throws MessagingException {
		var emailSpec = new EmailSpec();
		var build = cl.rehydrate(emailSpec, cl, this);
		build.setResolveStrategy(Closure.DELEGATE_FIRST);
		build.call();

        createEmail(emailSpec);
	}

	public void createEmail(EmailSpec spec) throws MessagingException {
		Function<Contact, Boolean> collision = spec.getCreatorKey() == null ? c -> true :
				(contact) -> NeedToSendEmail.valueOf(auditService, spec.getKeyCollision(), spec.getCreatorKey(), cayenneService.getNewContext(), contact).get();

		if (spec.getRecipients().isEmpty()) {
			SendEmailViaSmtp.valueOf(spec, cayenneService.getNewContext(), templateService, mailDeliveryService, collision).send();
		} else {
			SendEmailViaMessage.valueOf(spec, cayenneService.getNewContext(), templateService, preferenceController, collision).send();
		}
    }

	@Deprecated
	public void smtp(@DelegatesTo(SmtpSpec.class) Closure cl) throws MessagingException {
		var smtpSpec = new SmtpSpec();
		var build = cl.rehydrate(smtpSpec, cl, this);
		build.setResolveStrategy(Closure.DELEGATE_FIRST);
		build.call();

		var parameters = new SmtpParameters(smtpSpec);
		var param = MailDeliveryParamBuilder.valueOf(parameters, templateService).build();
		mailDeliveryService.sendEmail(param);
	}

	/**
	 * Queues email messages basing on the specification passed in {@link MessageParameter}.
	 *
	 * @param messageParameter - specification of emails and recipients
	 * @return - message sending result containing messages which were not sent
	 */
	public MessageSendingResult queueMessage(MessageParameter messageParameter) {
		var result = new MessageSendingResult();

		for (var entry : messageParameter.getRecordRecipientMap().entrySet()) {
			var emailMessage = create(messageParameter.getMessageTemplateName());

			emailMessage.bind(ScriptParameters.from(StringUtils.uncapitalize(messageParameter.getEntity()),
					getRecord(messageParameter.getEntity(), entry.getKey(), emailMessage.getTemplate().getObjectContext()))
					.asMap());

			var recipient = SelectById.query(Contact.class, entry.getValue()).selectOne(emailMessage.getTemplate().getObjectContext());
			var createdBy = SelectById.query(SystemUser.class, messageParameter.getUserId()).selectOne(emailMessage.getTemplate().getObjectContext());

			emailMessage.createdBy(createdBy);
			emailMessage.to(recipient);
			emailMessage.from(preferenceController.getEmailFromAddress());

			if (!emailMessage.send()) {
				result.addUnsentMessage(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	private PersistentObjectI getRecord(String entityName, Long recordId, ObjectContext context) {
		var entityClass = context.getEntityResolver()
				.getClassDescriptor(entityName).getEntity().getJavaClass();

		return (PersistentObjectI) SelectById.query(entityClass, recordId).selectOne(context);
	}
}
