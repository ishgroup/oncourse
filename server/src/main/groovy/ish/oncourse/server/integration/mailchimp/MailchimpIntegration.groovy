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

import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.util.LocalDateUtils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate

@CompileDynamic
@Plugin(type = 2)
class MailchimpIntegration implements PluginTrait {
	public static final String MAILCHIMP_API_KEY = "apiKey"
	public static final String MAILCHIMP_LIST_ID = "listId"

	String authHeader
	String membersUrl
	String region
	String listId

	private static Logger logger = LogManager.logger

	MailchimpIntegration(Map args) {
		loadConfig(args)

		this.listId = configuration.getIntegrationProperty(MAILCHIMP_LIST_ID).value
		this.authHeader = "Basic ${"login:${configuration.getIntegrationProperty(MAILCHIMP_API_KEY).value.tokenize('-').first()}".bytes.encodeBase64().toString()}"
		this.region = configuration.getIntegrationProperty(MAILCHIMP_API_KEY).value.tokenize('-').last()
		this.membersUrl = "https://${region}.api.mailchimp.com/3.0/lists/$listId/members/"
	}

	protected searchClient(String email) {
		RESTClient httpClient = new RESTClient(membersUrl + DigestUtils.md5Hex(email))

		httpClient.request(Method.GET, ContentType.JSON) {
			headers.'Authorization' = authHeader
			response.success = { resp, result ->
				return result
			}
			response.failure = { resp, result ->
				logger.error("Mailchimp search client failed: ${result.status} - ${result.title}: ${result.detail}. More information at: ${result.type}.")
			}
		}

	}

	protected addToList(String email, Map mergeFields,  String status) {
		RESTClient httpClient = new RESTClient(membersUrl)

		httpClient.request(Method.POST, ContentType.JSON) {
			headers.'Authorization' = authHeader
			body = [
					status: status,
					email_address: email,
					merge_fields: mergeFields.size() > 0 ? mergeFields : null,
			]

			response.success = { resp, result ->
				return result
			}
			response.failure = { resp, result ->
				logger.error("Mailchimp subscribe to list failed: ${result.status} - ${result.title}: ${result.detail}. More information at: ${result.type}.")
			}
		}

	}


	protected updateClient(String email, Map mergeFields = [:], String status) {
		RESTClient httpClient = new RESTClient(membersUrl + DigestUtils.md5Hex(email))

		httpClient.request(Method.PATCH, ContentType.JSON) {
			headers.'Authorization' = authHeader
			body = [
					status: status,
					email_address: email,
					merge_fields: mergeFields.size() > 0 ? mergeFields : null,
			]

			response.success = { resp, result ->
				return result
			}
			response.failure = { resp, result ->
				logger.error("Mailchimp update client failed: ${result.status} - ${result.title}: ${result.detail}. More information at: ${result.type}.")
			}
		}

	}

	def deletePermanently(String email) {
		def httpClient = new RESTClient(membersUrl + DigestUtils.md5Hex(email) + "/actions/delete-permanent")

		httpClient.request(Method.POST, ContentType.JSON) {
			headers.'Authorization' = authHeader

			response.success = { resp, result ->
				return result
			}
			response.failure = { resp, result ->
				logger.error("Mailchimp deleting subscriber from list failed: ${result.status} - ${result.title}: ${result.detail}. More information at: ${result.type}.")
			}
		}
	}

	String tag(String tagName, String email) {

		RESTClient httpClient = new RESTClient(getTagsUrl(email))
		httpClient.request(Method.POST, ContentType.JSON) {
			headers.'Authorization' = authHeader
			body = [
						tags: [ [name: tagName, status: "active"] ]
					]

			response.failure = { resp, result ->
				String error = "Mailchimp POST tags request failed: ${result.status} - ${result.title}: ${result.detail}. More information at: ${result.type}."
				logger.error(error)
				throw new IllegalStateException(error)
			}
		}
	}

	String getTagsUrl(String email) {
		return "https://${region}.api.mailchimp.com/3.0/lists/${listId}/members/${DigestUtils.md5Hex(email)}/tags"

	}

	protected List<String> getUnsubscribers(LocalDate since, Integer offset, Integer count) {
		List emails = []
		RESTClient httpClient = new RESTClient(membersUrl)
		httpClient.request(Method.GET, ContentType.JSON) {
			headers.'Authorization' = authHeader
			uri.query = [fields: "members.email_address",
						 unsubscribed_since: since ? LocalDateUtils.timeValueToString(since.atStartOfDay()) : "1970-09-02T10:06:35.977Z",
						 offset: offset,
						 count: count
						]
			response.success = { resp, result ->
				def members = result['members']
				if (members) {
					members.each {emails << it['email_address']}
				}
			}
			response.failure = { resp, result ->
				String error = "Mailchimp POST tags request failed: ${result.status} - ${result.title}: ${result.detail}. More information at: ${result.type}."
				logger.error(error)
				throw new IllegalStateException(error)
			}
		}

		return emails

	}
}
