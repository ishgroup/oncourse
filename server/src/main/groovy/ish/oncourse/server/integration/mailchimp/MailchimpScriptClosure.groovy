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

package ish.oncourse.server.integration.mailchimp

import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.scripting.ScriptClosureTrait
import ish.oncourse.server.scripting.ScriptClosure
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate
/**
 * Mailchimp integration groovy script API. Allows to subscribe/unsubscribe contact to/from Mailchimp list.
 * You can use 'subscribe' action for updating contacts in MailChimp.
 * 'Unsubscribe' deletes permanently contact from the list.
 *
 * Usage examples:
 *
 * Subscribe person to Mailchimp list using onCourse contact record:
 * ```
 * mailchimp {
 *     name "List1"
 *     action "subscribe"
 *     contact enrolment.student.contact
 * }
 * ```
 * Name property here is the name of Mailchimp integration which should be created in onCourse beforehand.
 * If the name isn't specified, script will execute it for all created 'MailChimp' integrations.
 *
 * Subscribe person without onCourse contact record to Mailchimp list:
 * ```
 * mailchimp {
 *     name "List2"
 *     action "subscribe"
 *     email "student@example.com"
 *     firstName "John"
 *     lastName "Doe"
 *     optIn false
 * }
 * ```
 *
 * Unsubscribe person from Mailchimp list:
 * ```
 * mailchimp {
 *     name "List1"
 *     action "unsubscribe"
 *     email "student@example.com"
 * }
 * ```
 *
 * The ability to create Mailchimp tags:
 *
 * ```
 * mailchimp {
 *     action "subscribe"
 *     contact enrolment.student.contact
 *     tags enrolment.student.contact.tags
 * }
 * ```
 * or any list of string
 *
 * ```
 * mailchimp {
 *     action "subscribe"
 *     contact enrolment.student.contact
 *     tags 'art', 'IT', 'prhotoshop'
 * }
 * ```
 *
 * Pull unsubscribers and mark the email address in onCourse as not allowed for marketing. Since param support both: LocalDate or string literal.
 *
 * ```
 * mailchimp {
 *     action "pull unsubscribes"
 *     since "2020-09-04"
 * }
 * ```
 *
 * Don't pass 'since' param to get all unsubscribes
 *
 * ```
 * mailchimp {
 *     action "pull unsubscribes"
 * }
 * ```
 * Delete a user permanently from MailChimp by email, after this the user can be added again
 *
 * ```
 * mailchimp {
 *     action "delete permanently"
 *     email "student@example.com"
 * }
 * ```
 *
 */
@API
@CompileStatic
@ScriptClosure(key = "mailchimp", integration = MailchimpIntegration)
class MailchimpScriptClosure implements ScriptClosureTrait<MailchimpIntegration> {

	static final SUBSCRIBE = "subscribe"
	static final UNSUBSCRIBE = "unsubscribe"
	static final PULL_UNSUBSCRIBES ="pull unsubscribes"
	static final DELETE_PERMANENTLY ="delete permanently"
    String action = SUBSCRIBE

    String email
    String firstName
    String lastName
	List<String> tags
	Map mergeFields = [:]
	LocalDate since

	private static Logger logger = LogManager.logger

	boolean optIn = true

	/**
	 * Set Mailchimp list action: "subscribe" or "unsubscribe" or "pull unsubscribes".
	 *
	 * @param action list action string, can be one of "subscribe" or "unsubscribe" or "pull unsubscribes"
     */
	@API
	void action(String action) {
		this.action = action
	}

	/**
	 * Set subscriber's email.
	 *
	 * @param email subscriber's email
     */
	@API
	void email(String email) {
		this.email = email
	}

	/**
	 * Set subscriber's first name.
	 *
	 * @param firstName first name of the subscriber
     */
	@API
	void firstName(String firstName) {
		if (firstName) {
			this.firstName = firstName
			this.mergeFields['FNAME'] = firstName
		}
	}

	/**
	 * Set subscriber's last name.
	 *
	 * @param lastName last name of the subscriber
     */
	@API
	void lastName(String lastName) {
		if (lastName) {
			this.lastName = lastName
			this.mergeFields['LNAME'] = lastName
		}
	}

	/**
	 * Specify contact to take subscriber's email, first and last name from.
	 *
	 * @param contact subscriber contact
     */
	@API
	void contact(Contact contact) {
		this.email = contact.email
		firstName(contact.firstName)
		lastName(contact.lastName)
	}

	/**
	 * A flag used to specify if a user must confirm via email before they are subscribed to a mailing list
	 * 'true' as default
	 *
	 * @param optIn for confirm email
	 */
	@API
	void optIn(boolean optIn = true) {
		this.optIn = optIn
	}

	/**
	 * The ability to create Mailchimp tags. You can pass any list of tags to the integration.
	 * If you want to map those in some way first, you can do that in the script you write.
	 * @param tags list
	 */
	@API
	void tags(List<Tag> tags) {
		this.tags = tags*.name
	}

	/**
	 * The ability to create Mailchimp tags. You can pass any list of strings to the integration.
	 * If you want to map those in some way first, you can do that in the script you write.
	 * @param tags list
	 */
	@API
	void tags(String... tags) {
		this.tags = Arrays.asList(tags)
	}

	/**
	 * Pull all unsubscribers since certain timestamp
	 * @param since date time
	 */
	@API
	void since(String since) {
		this.since = LocalDate.parse(since)
	}

	/**
	 * Pull all unsubscribers since certain timestamp
	 * @param since date time
	 */
	@API
	void since(LocalDate since) {
		this.since = since
	}

	@Override
	Object execute(MailchimpIntegration integration) {

		switch (action) {
			case MailchimpScriptClosure.SUBSCRIBE:
			case MailchimpScriptClosure.UNSUBSCRIBE:
				if (!email) {
					logger.error("Subscriber email is null, firstName: ${firstName}, lastName: ${lastName}. Abort script.")
					break
				}
				String status = MailchimpScriptClosure.UNSUBSCRIBE == action ? 'unsubscribed' : optIn ? 'pending' : 'subscribed'
				def result
				def client = integration.searchClient(email)
				if (client) {
					result = integration.updateClient(email, mergeFields, status)
				} else {
					result = integration.addToList(email, mergeFields, status)
				}

				if (result && tags && MailchimpScriptClosure.SUBSCRIBE == action) {
					tags.each { tag -> integration.tag(tag, email)}
				}
				break
			case MailchimpScriptClosure.DELETE_PERMANENTLY:
				if (!email) {
					logger.error("Subscriber email is null, firstName: ${firstName}, lastName: ${lastName}. Abort script.")
					break
				}
				integration.deletePermanently(email)
				break
			case MailchimpScriptClosure.PULL_UNSUBSCRIBES:
				int offset = 0
				int count = 200
				ObjectContext context = integration.cayenneService.newContext

				while (true) {
					List<String> emails = integration.getUnsubscribers(since, offset, count)
					offset += count
					ObjectSelect.query(Contact)
							.where(Contact.EMAIL.in(emails))
							.select(context)
							.each { it.allowEmail = false }
					context.commitChanges()
					if (emails.size() < count) {
						break
					}
				}
				break
			default:
				throw new IllegalArgumentException("Unknown action: ${action}")
		}
		return null
	}
}
