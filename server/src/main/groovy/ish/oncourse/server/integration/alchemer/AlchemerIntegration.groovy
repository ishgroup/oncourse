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
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
@Plugin(type = 4)
class AlchemerIntegration implements PluginTrait {
	public static final String ALCHEMER_USER = "user"
	public static final String ALCHEMER_PASSWORD = "password"
	public static final String ALCHEMER_SURVEY_ID = "surveyId"
	public static final String ALCHEMER_COURSE_TAG = "courseTag"
	public static final String ALCHEMER_SEND_ON_ENROLMENT_SUCCESS = "sendOnEnrolmentSuccess"
	public static final String ALCHEMER_SEND_ON_ENROLMENT_COMPLETION = "sendOnEnrolmentCompletion"

	static final String BASE_URL = "https://restapi.surveygizmo.com/v4/"

	def user
	def password
	def surveyId

	private static Logger logger = LogManager.logger

	AlchemerIntegration(Map args) {
		loadConfig(args)

		this.user = configuration.getIntegrationProperty(ALCHEMER_USER).value
		this.password = configuration.getIntegrationProperty(ALCHEMER_PASSWORD).value
		this.surveyId = configuration.getIntegrationProperty(ALCHEMER_SURVEY_ID).value
	}

	/**
	 * @param name
	 * @return the campaign id
	 */
	protected String createCampaign(String name) {
		def httpClient = new RESTClient(BASE_URL)

		httpClient.request(Method.GET, ContentType.JSON) {
			uri.path = "survey/${surveyId}/surveycampaign/"
			uri.query = [
			        _method: "PUT",
					"user:pass": "${user}:${password}",
					type: "email",
					name: name
			]

			response.success = { resp, result ->
				return result.data.id
			}
		} as String
	}

	protected boolean addContact(String campaignId, String email, String firstName, String lastName, String customId) {
		def httpClient = new RESTClient(BASE_URL)

		httpClient.request(Method.GET, ContentType.JSON) {
			uri.path = "survey/${surveyId}/surveycampaign/${campaignId}/contact/"
			uri.query = [
			        _method: "PUT",
					"user:pass": "${user}:${password}",
					semailaddress: email,
					sfirstName: firstName,
					slastName: lastName,
					scustomfield10: customId
			]

			response.success = { resp, result ->
				return result.result_ok == true
			}
		}
	}

	protected sendEmail(String campaignId, String subject, String emailBody, String replyTo) {
		def httpClient = new RESTClient(BASE_URL)

		httpClient.request(Method.GET, ContentType.JSON) {
			uri.path = "survey/${surveyId}/surveycampaign/${campaignId}/emailmessage/"
			uri.query = [
			        _method: "PUT",
					"user:pass": "${user}:${password}",
					type: "message",
					"from[email]": replyTo,
					replies: replyTo,
					subject: subject,
					messagetype: "plaintext",
					"body[text]": emailBody,
					sfootercopy: "This message was sent by [account(\"physical address\")].",
					send: true
			]

			response.success = { resp, result ->
				return result
			}
		}
	}

}
