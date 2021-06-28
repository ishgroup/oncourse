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

package ish.oncourse.server.integration.alchemer

import groovy.transform.CompileDynamic
import ish.oncourse.API
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.EmailTemplate
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait

/**
 * This integration allows you to push records into Alchemer in order to add them to a survey. Create one integration
 * per survey you want to send and then use this scripting block:
 *
 * alchemer {
 *     contact myContact
 *     template myTemplate
 *     reply "info@acme.com"
 * }
 *
 * Use the name option if you have more than one Alchemer integration and you want to push to only one.
 * ```
 * alchemer {
 * 	   name "name of integration"
 *     contact myContact
 *     template myTemplate
 *     reply "info@acme.com"
 * }
 **/
@CompileDynamic
@API
@ScriptClosure(key = "alchemer", integration = AlchemerIntegration)
class AlchemerScriptClosure implements ScriptClosureTrait<AlchemerIntegration> {

    String email
    String firstName
    String lastName
    String customId

	String template

    String subject
    String emailBody

    String replyTo

	def email(String email) {
		this.email = email
	}

	def firstName(String firstName) {
		this.firstName = firstName
	}

	def lastName(String lastName) {
		this.lastName = lastName
	}

	def customId(String customId) {
		this.customId = customId
	}

	def customId(Long customId) {
		this.customId = String.valueOf(customId)
	}

	def subject(String subject) {
		this.subject = subject
	}

	def body(String emailBody) {
		this.emailBody = emailBody
	}

	def reply(String replyTo) {
		this.replyTo = replyTo
	}

	def contact(Contact contact) {
		email(contact.email)
		firstName(contact.firstName)
		lastName(contact.lastName)
		customId(contact.id)
	}

	def template(String template) {
		this.template = template
	}

	@Override
	Object execute(AlchemerIntegration integration) {
		def campaign_id = integration.createCampaign("${firstName} ${lastName} <${email}>")

		if (campaign_id) {
			def success = integration.addContact(campaign_id, email, firstName, lastName, customId)

			if (success) {
				if (template) {
					EmailTemplate template = integration.templateService.loadTemplate(template)

					if (template == null) {
						throw new IllegalArgumentException("No template with name '${template}' was found.")
					}

					subject = template.subject
					emailBody = integration.templateService.renderPlain(template, [
							surveyLink: "[invite(\"survey link\")]",
							unsubscribeLink: "[invite(\"unsubscribe link\")]",
							type: "SurveyGizmo"
					])
				}

				integration.sendEmail(campaign_id, subject, emailBody, replyTo)
			}
		}
		
		return null
	}
}
