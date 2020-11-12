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

package ish.oncourse.server.messaging

import com.google.inject.Inject
import groovy.transform.CompileDynamic
import ish.common.types.MessageStatus
import ish.common.types.MessageType
import ish.messaging.SendMessageRequest
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.service.EmailTemplateApiService
import ish.oncourse.server.scripting.api.MailDeliveryParamBuilder
import ish.oncourse.server.scripting.api.SmtpParameters

import static ish.oncourse.server.api.v1.function.MessageFunctions.getEntityTransformationProperty
import static ish.oncourse.server.api.v1.function.MessageFunctions.getRecipientsListFromEntity
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.EmailTemplate
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.scripting.api.MessageSpec
import ish.oncourse.server.scripting.api.SmsSpec
import ish.oncourse.server.scripting.api.TemplateService
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import javax.mail.MessagingException

@CompileDynamic
class MessageService {

	ICayenneService cayenneService
    PreferenceController preferenceController
	TemplateService templateService
	EmailTemplateApiService templateApiService
	MailDeliveryService mailDeliveryService

    @Inject
    MessageService(ICayenneService cayenneService, PreferenceController preferenceController, TemplateService templateService,
				   EmailTemplateApiService templateApiService, MailDeliveryService mailDeliveryService) {
		this.cayenneService = cayenneService
        this.preferenceController = preferenceController
        this.templateService = templateService
        this.templateApiService = templateApiService
		this.mailDeliveryService = mailDeliveryService
    }

	def sendMessage(SendMessageRequest request) {
		def context = cayenneService.newContext

        def message = context.newObject(Message)

		message.emailSubject = request.emailSubject
		message.emailBody = request.emailBody
		message.emailHtmlBody = request.emailHtmlBody
		message.smsText = request.smsText
		message.postDescription = request.postDescription
		message.emailFrom = request.emailFrom ?: preferenceController.emailFromAddress

		if (request.createdById != null) {
			message.createdBy = SelectById.query(SystemUser, request.createdById).selectOne(context)
		}

		def counter = 0
		request.recipients.each { recipientId, sendingPreference ->
			def recipient = SelectById.query(Contact, recipientId).selectOne(context)

			if (sendingPreference.sendEmail) {
				createMessagePerson(message, recipient, MessageType.EMAIL)
			}
			if (sendingPreference.sendSms) {
				createMessagePerson(message, recipient, MessageType.SMS)
			}
			if (sendingPreference.sendPost) {
				createMessagePerson(message, recipient, MessageType.POST)
			}

			if (++counter == 100) {
				context.commitChanges()
				counter=0
			}
		}

		if (counter > 0) {
			context.commitChanges()
		}
	}

	def static createMessagePerson(Message message, Contact contact, MessageType type) {
		def context = contact.objectContext

		def messagePerson
		switch (type) {
			case MessageType.SMS:
				if (!contact.mobilePhone) {
					return
				}
				messagePerson = context.newObject(MessagePerson)
				messagePerson.destinationAddress = contact.mobilePhone
				break
			case MessageType.EMAIL:
				if (!contact.email) {
					return
				}
				messagePerson = context.newObject(MessagePerson)
				messagePerson.destinationAddress = contact.email
				break
			case MessageType.POST:
				throw new UnsupportedOperationException()
		}
		messagePerson.attemptCount = 0
		messagePerson.message = message
		messagePerson.status = MessageStatus.QUEUED
		messagePerson.type = type

		messagePerson.contact = contact
	}

	/**
	 * This helper method makes it easy to send SMS messages. Please refer to the usage example below:
	 *
	 * ```
	 *  sms {
	 *      to enrolment.student.contact
	 *      text "Your enrolment has been successful."
	 *  }
	 * ```
	 *
	 * Note that passed contact records must have mobile phone property set, otherwise sending will fail.
	 */
	def sms(@DelegatesTo(SmsSpec) Closure cl) {
		SmsSpec smsSpec = new SmsSpec()
		Closure build = cl.rehydrate(smsSpec, cl, this)
		build.resolveStrategy = Closure.DELEGATE_FIRST
		build()

		SendMessageRequest sendMessageRequest = new SendMessageRequest()
        sendMessageRequest.smsText = smsSpec.text
		smsSpec.recipients.each { recipient ->
			sendMessageRequest.addRecipient(recipient, new SendMessageRequest.SendingPreference(sendSms: true))
		}

		sendMessage(sendMessageRequest)
	}



	/**
	 * Script API method allowing calling message execution from groovy code.
	 */
	void message(@DelegatesTo(MessageSpec) Closure cl) throws MessagingException {
		MessageSpec messageSpec = new MessageSpec()
		def build = cl.rehydrate(messageSpec, cl, this)
		build.setResolveStrategy(Closure.DELEGATE_FIRST)
		build.call()

		if (!messageSpec.records.isEmpty()) {
			sendMessage(messageSpec)
		}
	}


	void sendMessage(MessageSpec messageSpec) {
		EmailTemplate template = templateService.loadTemplate(messageSpec.templateName)
		if (!template) {
			throw new IllegalArgumentException("No template with keyCode ${messageSpec.templateName} found.")
		}
		ObjectContext context = template.getContext()
		Map<String, Object> bindings = messageSpec.bindings
		List<CayenneDataObject> records = []
		String entityName = (messageSpec.records[0] as CayenneDataObject).entityName.capitalize()
		String templateEntityName = template.entity?.capitalize()
		if (templateEntityName != null && entityName != templateEntityName) {
			Property<Long> property = getEntityTransformationProperty(entityName, templateEntityName)
			if (!property) {
				throw new IllegalArgumentException("$template.name is not valid for $entityName records")
			}
			Class<? extends CayenneDataObject> clazz = EntityUtil.entityClassForName(templateEntityName)
			List<Long> ids = messageSpec.records.collect { (it as CayenneDataObject).id }
			records = ObjectSelect.query(clazz).where(property.in(ids)).select(context)
		} else {
			records = messageSpec.records.collect { it as CayenneDataObject } as List<CayenneDataObject>
		}


		int counter = 0
		records.each { record ->
			List<Contact> recipients = getRecipientsListFromEntity(record)

			recipients.each { recipient ->

				bindings.put(entityName.uncapitalize(), record)
				bindings.put(templateService.RECORD, record)
				bindings.put(templateService.TO, recipient)

				if (templateEntityName != null && templateEntityName.equalsIgnoreCase("Contact")) {
					bindings.put(templateEntityName.uncapitalize(), recipient)
					bindings.put(templateService.RECORD, recipient)
				}
				
				Message message = null

				switch (template.type) {
					case MessageType.EMAIL:

						String subject = templateService.renderSubject(template, bindings)
						bindings.put(templateService.SUBJECT, subject)
						
						
						if (messageSpec.attachments.empty) {
							message = context.newObject(Message.class)
							message.emailSubject = subject
							message.emailBody = templateService.renderPlain(template, bindings)
							message.emailHtmlBody = templateService.renderHtml(template, bindings)
							message.emailFrom = messageSpec.fromAddress?:preferenceController.emailFromAddress
							
						} else if (recipient.email) {

							SmtpParameters parameters = new SmtpParameters(
									messageSpec.fromAddress?:preferenceController.emailFromAddress,
									preferenceController.emailFromName,
									messageSpec.bccList,
									recipient.email,
									template.keyCode, bindings, messageSpec.attachments )
							mailDeliveryService.sendEmail(MailDeliveryParamBuilder.valueOf(parameters, templateService).build());
						}
						break
					case MessageType.SMS:
						message = context.newObject(Message.class)
						message.smsText = templateService.renderPlain(template, bindings)
						break
				}
				if (message) {
					createMessagePerson(message, context.localObject(recipient), template.type)
				}
			}
			if (++counter == 50) {
				context.commitChanges()
				counter = 0
			}
		}
		if (counter > 0) {
			context.commitChanges()
		}
	}
}
