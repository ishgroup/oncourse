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

package ish.oncourse.server.integration.surveymonkey

import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
@Plugin(type = 3)
class SurveyMonkeyIntegration implements PluginTrait {
	public static final String SURVEYMONKEY_AUTH_TOKEN = "authToken"
	public static final String SURVEYMONKEY_SURVEY_NAME = "surveyName"
	public static final String SURVEYMONKEY_COURSE_TAG = "courseTag"
	public static final String SURVEYMONKEY_SEND_ON_ENROLMENT_SUCCESS = "sendOnEnrolmentSuccess"
	public static final String SURVEYMONKEY_SEND_ON_ENROLMENT_COMPLETION = "sendOnEnrolmentCompletion"

	static final BASE_URL = 'https://api.surveymonkey.net'
	static final COLLECTOR_NAME = 'onCourse'

	def authToken
	def surveyName

	private static Logger logger = LogManager.logger

	SurveyMonkeyIntegration(Map args) {
		loadConfig(args)

		this.authToken = configuration.getIntegrationProperty(SURVEYMONKEY_AUTH_TOKEN).value
		this.surveyName = configuration.getIntegrationProperty(SURVEYMONKEY_SURVEY_NAME).value
	}

	/**
	 * Retrieves a paged list of surveys in a user's account.
	 */
    Object getSurveyId(String surveyTitle) {
		def httpClient = new RESTClient(BASE_URL)

		httpClient.headers['Authorization'] = "bearer ${authToken}"

		httpClient.request(Method.GET, ContentType.JSON) {
			uri.path = "/v3/surveys/"
			uri.query = [title: surveyTitle]

			response.success = { resp, result ->
				return result
			}
		}
	}

	/**
	 * Retrieves a paged list of collectors for a survey in a user's account.
	 */
    Object getCollectorList(String survey_id) {
		def httpClient = new RESTClient(BASE_URL)

		httpClient.headers['Authorization'] = "bearer ${authToken}"

		httpClient.request(Method.GET, ContentType.JSON) {
			uri.path = "/v3/surveys/${survey_id}/collectors/".toString()
			uri.query = [include: ['status,type'] ]

			response.success = { resp, result ->
				return result
			}
		}

	}

    Object getMessage(String collectorId) {
		def httpClient = new RESTClient(BASE_URL)

		httpClient.headers['Authorization'] = "bearer ${authToken}"

		httpClient.request(Method.POST, ContentType.JSON) {
			uri.path = "/v3/collectors/${collectorId}/messages/".toString()

			body = [
					type : 'invite'
			]

			response.success = { resp, result ->
				return result
			}
		}

	}

    Object getRecipient(String collectorId, String messageId, String email, String firstName, String lastName) {
		def httpClient = new RESTClient(BASE_URL)

		httpClient.headers['Authorization'] = "bearer ${authToken}"

		httpClient.request(Method.POST, ContentType.JSON) {
			uri.path = "/v3/collectors/${collectorId}/messages/${messageId}/recipients".toString()

			body = [
					email : email,
					first_name : firstName,
					last_name : lastName
			]

			response.success = { resp, result ->
				return result
			}
		}

	}


    Object send(String collectorId, String messageId) {
		String now = new Date().format("yyyy-MM-dd'T'hh:mm:ssXXX")
		def httpClient = new RESTClient(BASE_URL)

		httpClient.headers['Authorization'] = "bearer ${authToken}"

		httpClient.request(Method.POST, ContentType.JSON) {
			uri.path = "/v3/collectors/${collectorId}/messages/${messageId}/send".toString()

			body = [scheduled_date : now ]

			response.failure = { resp, result ->
				println result
				return result
			}

			response.success = { resp, result ->
				return result
			}
		}

	}


	protected sendSurvey(String email, String firstName, String lastName) {
		def surveyId = getSurveyId(surveyName).data[0]?.id

		if (surveyId) {
			//get all email collectors
			def allEmailCollectors = getCollectorList(surveyId).data?.findAll {c ->
				c.status == 'open' && c.type == 'email'
			}

			//find collector_id with name 'onCourse'
			String collectorId = allEmailCollectors?.find { c ->
				c.name == COLLECTOR_NAME
			}?.id

			if (!collectorId) {
				collectorId  = allEmailCollectors[0]?.id
			}

			if (collectorId) {
				String messageId = getMessage(collectorId).id
				String recipientId = getRecipient(collectorId, messageId, email, firstName, lastName).id
				send(collectorId, messageId)
			}
		}
	}
}
