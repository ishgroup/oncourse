/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import groovy.json.JsonOutput
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

def run(args) {
	def enrolment = args.entity

	if (enrolment.status == EnrolmentStatus.SUCCESS && enrolment.confirmationStatus == ConfirmationStatus.NOT_SENT) {
		def m = Email.create("Enrolment Confirmation")
		m.bind(enrolment: enrolment)
		m.to(enrolment.student.contact)

		m.send()

		enrolment.setConfirmationStatus(ConfirmationStatus.SENT)
		args.context.commitChanges()

		createMoodleEnrolment(enrolment)
	}
}

def createMoodleEnrolment(enrolment) {
	def contact = enrolment.student.contact

	if (contact.email) {
		def users = getUserByEmail(contact.email)
		def userId = null

		if (!users.isEmpty()) {
			userId = users[0].id
		} else {
			// TODO: what to set as Moodle user name and password?
			userId = createUser(contact.lastName, contact.password, contact).id
		}

		def course = getCourse(enrolment.courseClass.course.code)

		if (course) {
			enrolUsers(userId, course.id[0])
		}
	}
}

def getUserByEmail(String email) {
	def httpClient = new RESTClient("https://m.openmq.com.au/webservice/rest/server.php")

	httpClient.request(Method.GET, ContentType.JSON) {
		uri.query = [wsfunction: "core_user_get_users",
					 wstoken: getToken(),
					 moodlewsrestformat: "json",
					 "criteria[0][key]": "email",
					 "criteria[0][value]": email]

		response.success = { resp, result ->
			return result.users
		}
	}
}

def createUser(userName, userPassword, contact) {
	def httpClient = new RESTClient("https://m.openmq.com.au/webservice/rest/server.php")

	httpClient.request(Method.POST, ContentType.JSON) {
		uri.query = [wsfunction: "core_user_create_users",
					 wstoken: getToken(),
					 moodlewsrestformat: "json",
					 "users[0][username]": userName,
					 "users[0][password]": userPassword,
					 "users[0][firstname]": contact.firstName,
					 "users[0][lastname]": contact.lastName,
					 "users[0][email]": contact.email,
					 "users[0][auth]": "manual",
					 "users[0][city]": contact.suburb,
					 "users[0][country]": "AU"]

		response.success = { resp, result ->
			return result
		}
	}
}

def getCourse(String code) {
	def httpClient = new RESTClient("https://m.openmq.com.au/webservice/rest/server.php")

	httpClient.request(Method.GET, ContentType) {
		uri.query = [wsfunction: "local_wsoncourse_get_courses_by_idnumber",
					 wstoken: getToken(),
					 moodlewsrestformat: "json",
					 "options[idnumbers][0]": code]

		response.success = { resp, result ->
			return result
		}
	}
}

def enrolUsers(userId, courseId) {
	def httpClient = new RESTClient("https://m.openmq.com.au/webservice/rest/server.php")

	httpClient.request(Method.POST, ContentType.JSON) {
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

def getToken() {
	def httpClient = new RESTClient("https://m.openmq.com.au/login/token.php")

	httpClient.request(Method.GET, ContentType.JSON) {
		uri.query = [username: "ish", password: "6mYbVO4HAP4", service: "Ish"]

		response.success = { resp, result ->
			return result.token
		}
	}
}
