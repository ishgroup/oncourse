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

package ish.oncourse.server.integration.moodle

import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import static org.apache.commons.lang3.StringUtils.EMPTY
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
@Plugin(type = 1)
class MoodleIntegration implements PluginTrait {
	public static final String MOODLE_BASE_URL_KEY = "baseUrl"
	public static final String MOODLE_USERNAME_KEY = "username"
	public static final String MOODLE_PASSWORD_KEY = "password"
	public static final String MOODLE_SERVICE_NAME_KEY = "serviceName"

	def baseUrl
	def username
	def password
	def serviceName

	private static Logger logger = LogManager.logger

	MoodleIntegration(Map args) {
		loadConfig(args)

		this.username = configuration.getIntegrationProperty(MOODLE_USERNAME_KEY).value
		this.password= configuration.getIntegrationProperty(MOODLE_PASSWORD_KEY).value
		this.baseUrl = configuration.getIntegrationProperty(MOODLE_BASE_URL_KEY).value
		this.serviceName = configuration.getIntegrationProperty(MOODLE_SERVICE_NAME_KEY).value
	}

	private List<?> getUserByEmail(String email) {
		def httpClient = new RESTClient(baseUrl)

		httpClient.request(Method.GET, ContentType.JSON) {
			uri.path = "/webservice/rest/server.php"
			uri.query = [wsfunction: "core_user_get_users",
						 wstoken: getToken(),
						 moodlewsrestformat: "json",
						 "criteria[0][key]": "email",
						 "criteria[0][value]": email.toLowerCase()]

			response.success = { resp, result ->
				if (result.users == null) {
					logger.error("Get user request failed {} {}", resp.toString(), result.toString())
					throw new RuntimeException("Get user request failed ${result.toString()}")
				}
				return result['users']
			}
		} as List<?>
	}

	protected Integer getUserIdByEmail(String email) {
		def users = getUserByEmail(email)
		if (users && users.empty) {
			return users[0]['id'] as Integer
		}
		return null
	}

	protected createUser(userName, userPassword, contact) {
		def httpClient = new RESTClient(baseUrl)

		httpClient.request(Method.POST, ContentType.JSON) {
			uri.path = "/webservice/rest/server.php"
			uri.query = [wsfunction: "core_user_create_users",
						 wstoken: getToken(),
						 moodlewsrestformat: "json",
						 "users[0][username]": userName.toLowerCase(),
						 "users[0][password]": userPassword,
						 "users[0][firstname]": contact.firstName.toLowerCase(),
						 "users[0][lastname]": contact.lastName.toLowerCase(),
						 "users[0][email]": contact.email.toLowerCase(),
						 "users[0][auth]": "manual",
						 "users[0][city]": contact.suburb != null ? contact.suburb.toLowerCase() : EMPTY,
						 "users[0][country]": "AU"]

			response.success = { resp, result ->
				if (result == null || result.empty) {
					logger.error("Create user request failed {} {}", resp.toString(), result.toString())
					throw new RuntimeException("Create user request failed ${result.toString()}")
				}
				return result[0]
			}
		}
	}

	protected getCourseBy(String courseName, Map<String, String> courseReference) {
		def httpClient = new RESTClient(baseUrl)

		String referenceName
		String referenceValue

		if (courseReference && !courseReference.isEmpty()) {
			referenceName = courseReference.entrySet()[0].key
			referenceValue = courseReference.entrySet()[0].value
		} else {
			referenceName = 'shortname'
			referenceValue = courseName
		}

		httpClient.request(Method.GET, ContentType.JSON) {
			uri.path = "/webservice/rest/server.php"
			uri.query = [wsfunction: "core_course_get_courses_by_field",
						 wstoken: getToken(),
						 field: referenceName,
						 value: referenceValue,
						 moodlewsrestformat: "json"]

			response.success = { resp, result ->
				if (result.courses == null || result.courses.empty) {
					logger.error("Get courses request failed {} {}", resp.toString(), result.toString())
					throw new RuntimeException("Get courses request failed ${result.toString()}")
				}
				return result.courses[0]
			}
		}
	}


    Object getCourses() {
		def httpClient = new RESTClient(baseUrl)

		httpClient.request(Method.GET, ContentType.JSON) {
			uri.path = "/webservice/rest/server.php"
			uri.query = [wsfunction: "core_course_get_courses",
						 wstoken: getToken(),
						 moodlewsrestformat: "json"]

			response.success = { resp, result ->
				return result
			}
		}
	}

	protected enrolUsers(userId, courseId) {
		def httpClient = new RESTClient(baseUrl)

		httpClient.request(Method.POST, ContentType.JSON) {
			uri.path = "/webservice/rest/server.php"
			uri.query = [wsfunction: "enrol_manual_enrol_users",
						 wstoken: getToken(),
						 moodlewsrestformat: "json",
						 "enrolments[0][roleid]": 5,
						 "enrolments[0][userid]": userId,
						 "enrolments[0][courseid]": courseId]

			response.success = { resp, result ->
				return result
			}
		}
	}

    Object getToken() {
		def httpClient = new RESTClient(baseUrl)

		httpClient.request(Method.GET, ContentType.JSON) {
			uri.path = "/login/token.php"
			uri.query = [username: username.toLowerCase(),
						 password: password,
						 service: serviceName.toLowerCase()]

			response.success = { resp, result ->
				if (result.token) {
					return result.token
				}
				logger.error("Authentification failed {} {}", resp.toString(), result.toString())
				throw new RuntimeException("Authentification failed ${result.toString()}")

			}
			response.failure = { resp, result ->
				logger.error("Authentification failed {} {}", resp.toString(), result.toString())
			}
		}
	}
}
