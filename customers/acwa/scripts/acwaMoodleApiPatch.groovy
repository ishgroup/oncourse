/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.util.SecurityUtil


baseUrl = "https://lms.acwa.asn.au/"
username = "oncourse"
password = "ccwt1"
serviceName = "onCourse"

def getUserByEmail(String email) {
    def httpClient = new RESTClient(baseUrl)

    httpClient.request(Method.GET, ContentType.JSON) {
        uri.path = "webservice/rest/server.php"
        uri.query = [wsfunction: "core_user_get_users",
                     wstoken: getToken(),
                     moodlewsrestformat: "json",
                     "criteria[0][key]": "email",
                     "criteria[0][value]": email.toLowerCase()]


        response.success = { resp, result ->
            return result.users
        }
    }
}

def getCourses() {
    def httpClient = new RESTClient(baseUrl)

    httpClient.request(Method.GET, ContentType.JSON) {
        uri.path = "webservice/rest/server.php"
        uri.query = [wsfunction: "core_course_get_courses",
                     wstoken: getToken(),
                     moodlewsrestformat: "json"]



        response.success = { resp, result ->
            return result
        }
    }
}

def getToken() {
    def httpClient = new RESTClient(baseUrl)

    httpClient.request(Method.GET, ContentType.JSON) {
        uri.path = "login/token.php"
        uri.query = [username: username.toLowerCase(),
                     password: password,
                     service: serviceName.toLowerCase()]

        response.success = { resp, result ->
            return result.token
        }
    }
}




def createUser(userName, userPassword, firstName, lastName) {
    def httpClient = new RESTClient(baseUrl)

    httpClient.request(Method.POST, ContentType.JSON) {
        uri.path = "webservice/rest/server.php"
        uri.query = [wsfunction: "core_user_create_users",
                     wstoken: getToken(),
                     moodlewsrestformat: "json",
                     "users[0][username]": userName.toLowerCase(),
                     "users[0][password]": userPassword,
                     "users[0][firstname]": firstName.toLowerCase(),
                     "users[0][lastname]": lastName.toLowerCase(),
                     "users[0][email]": userName.toLowerCase(),
                     "users[0][auth]": "manual",
                     "users[0][city]": "",
                     "users[0][country]": "AU"]

        response.success = { resp, result ->
            return result
        }
    }
}

def enrolUsers(userId, courseId) {
    def httpClient = new RESTClient(baseUrl)

    httpClient.request(Method.POST, ContentType.JSON) {
        uri.path = "webservice/rest/server.php"
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

def run(args) {
    def enrolment = args.entity

    if (enrolment.courseClass.course.hasTag("Moodle")) {

        if (enrolment.student.contact.email) {
            def users = getUserByEmail(enrolment.student.contact.email)
            def userId

            if (!users) {
                def userName = enrolment.student.contact.email.toLowerCase()
                userId = createUser(userName, SecurityUtil.generateRandomPassword(8), enrolment.student.contact.firstName, enrolment.student.contact.lastName).id
            } else {
                userId = users[0].id
            }

            def course = getCourses().find { c -> c.idnumber == enrolment.courseClass.course.code }

            if (course) {
                enrol = enrolUsers(userId, course["id"])
            }
        }
    }
}
