/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import groovy.json.JsonOutput
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.util.SecurityUtil

def run(args) {
  def enrolment = args.entity

  if (enrolment.status == EnrolmentStatus.SUCCESS && enrolment.confirmationStatus == ConfirmationStatus.NOT_SENT) {
    def m = Email.create("Enrolment Confirmation")
    m.bind(enrolment: enrolment)
    m.to(enrolment.student.contact)

    m.send()

    enrolment.setConfirmationStatus(ConfirmationStatus.SENT)
    args.context.commitChanges()

    if (enrolment.courseClass.course.code == "JSA" && enrolment?.invoiceLine?.invoice?.contact?.email != null) {
      sendSurvey(enrolment)
    }

    createMoodleEnrolment(enrolment)
  }
}

def sendSurvey(enrolment) {
  def client = HttpClients.createDefault()

  def sendFlowRequest = new HttpPost("https://api.surveymonkey.net/v2/batch/send_flow?api_key=wnpvgghmn2tnba4zr6etu8d5")

  sendFlowRequest.addHeader("Authorization",
      "bearer paTBz61kMlD0mPWrsaHR3921EuYbHlBvqU0GDZQ.5nahBvB1GbdZpbh2WnOzXY4SaovFBY6leML0jYh4WjB4lWjo9OU3FXjpCHITHSCUj4s=")

  sendFlowRequest.addHeader("Content-Type", "application/json")

  def body = new StringEntity(
      "{\n" +
          "    \"collector\": {\n" +
          "        \"name\": \"onCourse email invite\",\n" +
          "        \"recipients\": [\n" +
          "            {\n" +
          "                \"email\": \"${enrolment.invoiceLine.invoice.contact.email}\",\n" +
          "                \"first_name\": \"${enrolment.invoiceLine.invoice.contact.firstName}\",\n" +
          "                \"last_name\": \"${enrolment.invoiceLine.invoice.contact.lastName}\",\n" +
          "                \"custom_id\": \"${enrolment.student.studentNumber}\"\n" +
          "            }\n" +
          "        ],\n" +
          "        \"send\": true,\n" +
          "        \"type\": \"email\"\n" +
          "    },\n" +
          "    \"email_message\": {\n" +
          "        \"body_text\": \"Please complete a short survey to complete the enrolment by Monday, 22 June. \\n[SurveyLink] \\nThe information will be used to deliver and develop the program and ensure the safety of all children. Please complete one survey for each child attending. \\n[RemoveLink]\",\n" +
          "        \"reply_email\": \"${Preferences.get("email.from")}\",\n" +
          "        \"subject\": \"Junior Science Academy additional information required\"\n" +
          "    },\n" +
          "    \"survey_id\": \"65646963\"\n" +
          "}", ContentType.APPLICATION_JSON)

  sendFlowRequest.setEntity(body)

  client.execute(sendFlowRequest)
}

def createMoodleEnrolment(enrolment) {
  def contact = enrolment.student.contact

  if (contact.email) {
    def users = getUserByEmail(contact.email)
    def userId = null
    def password = null

    if (!users.isEmpty()) {
      userId = users[0].id
    } else {
      // create new user using email as login and generated random 8 character password
      password = SecurityUtil.generateRandomPassword(8)
      userId = createUser(contact.email, password, contact).id
      return
    }

    def course = getCourse(enrolment.courseClass.course.code)

    if (course) {
      enrolUsers(userId, course.id[0])

      email {
        template "eLearning"
        from preference.email.from
        to contact.email
        bindings contact: contact, course: enrolment.courseClass.course, username: contact.email, password: password
      }
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
