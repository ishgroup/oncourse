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

package ish.oncourse.server.integration.cloudassess

import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import ish.common.types.OutcomeStatus
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Module
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
@Plugin(type = 7)
class CloudAssessIntegration implements PluginTrait {
    public static final String CLOUDASSESS_USERNAME = "username"
    public static final String CLOUDASSESS_API_KEY = "apiKey"
    public static final String CLOUDASSESS_ORG_ID = "orgId"

    static final clientId = "041103224c6696aa4592aec26e2ac35d9b5bae1d817b8ac6b2c5cfb9c6f94fdf"
    static final clientSecret = "08f3154678c9e305865c0fa2aaeadf6ed012522ceb2ce853c3ba7541e545e2bd"

    static final BASE_URL = "https://www.assessapp.com"

    static final LOGIN_URL = "/api/authorise"
    static final MEMBERSHIP_URL = "/api/memberships"
    static final COURSE_URL = "/api/courses"
    static final COURSE_ENROLMENT_URL = "/api/course_enrolments"
    static final COMPLETED_UNIT_RECORDS_URL = "/api/completed_unit_records"
    static final COMPLETED_ASSESSMENT_RECORDS_URL = "/api/completed_assessment_records"

    static final DATE_FORMAT_PATTEN = "dd-MMM-yyyy"

    def username
    def apiKey
    def orgId

    private static Logger logger = LogManager.logger

    CloudAssessIntegration(Map args) {
        loadConfig(args)

        this.username = configuration.getIntegrationProperty(CLOUDASSESS_USERNAME).value
        this.apiKey = configuration.getIntegrationProperty(CLOUDASSESS_API_KEY).value
        this.orgId = configuration.getIntegrationProperty(CLOUDASSESS_ORG_ID).value
    }

    protected enrol(enrolment, code) {
        def accessToken = login().access_token

        def courseCode = code ?: enrolment.courseClass.course.code

        def courseId = courseSearch(accessToken, courseCode).response.find{ it -> it.code == courseCode}?.id

        if (courseId) {
            def membershipId = membershipSearch(accessToken, enrolment.student.contact.email).response.find{ it -> it.email == enrolment.student.contact.email}?.id
            membershipId = membershipId ?: membershipCreate(
                    accessToken,
                    enrolment.student.contact.email,
                    enrolment.student.contact.firstName,
                    enrolment.student.contact.lastName,
                    enrolment.student.studentNumber.toString(),
                    enrolment.student.contact.id).response.id

            enrolmentCreate(accessToken, courseId, membershipId).response.id
        }
    }

    protected course(code, courseName, qualification) {
        def accessToken = login().access_token

        def courseId = courseSearch(accessToken, code).response.find{ it -> it.code == code}?.id

        if (!courseId) {
            courseCreate(accessToken, code, courseName, qualification)
        }
    }

    protected outcomes(since) {
        def accessToken = login().access_token

        def fromDate = (since ?: Calendar.getInstance().getTime() - 1).format(DATE_FORMAT_PATTEN)
        def toDate = Calendar.getInstance().getTime().format(DATE_FORMAT_PATTEN)

        completedUnitRecords(accessToken, fromDate, toDate).response
    }

    protected static updatedOnCourseOutcomes(records) {
        def updatedOutcomes = []
        records.each { r ->
            def outcomes = ObjectSelect.query(Outcome)
                    .where(Outcome.ENROLMENT.dot(Enrolment.STUDENT).dot(Student.STUDENT_NUMBER).eq(r.student.student_number))
                    .and(Outcome.MODULE.dot(Module.NATIONAL_CODE).eq(r.unit.code))
                    .select(cayenneService.newContext)

            outcomes.each { o ->
                //2 = "Competent", 3 = "Not Yet Competent"
                def outcomeStatus = r.outcome_id == 2 ? OutcomeStatus.STATUS_ASSESSABLE_PASS : (r.outcome_id == 3 ? OutcomeStatus.STATUS_ASSESSABLE_FAIL : null)
                if (outcomeStatus && o.status != outcomeStatus) {
                    o.status = outcomeStatus
                    updatedOutcomes << o
                }
            }

        }

        updatedOutcomes
    }

    /**
     * @return [access_token, token_type, expires_in, refresh_token]
     */
    private login() {
        def httpClient = new IshRESTClient(BASE_URL)

        httpClient.request(Method.POST, ContentType.JSON) {
            uri.path = LOGIN_URL
            body = [
                    username: username,
                    api_key1: apiKey,
                    client_id: clientId,
                    client_secret: clientSecret
            ]

            response.success = { resp, result ->
                result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Login to Cloud Assess failed: ${result.response}")
            }
        }
    }

    /**
     * @param accessToken - returned from Login endpoint
     * @param lastName    - search parameter. last name of student
     * @return list of memberships with '30 - Student' for this organization
     */
    private membershipSearch(accessToken, email) {
        def httpClient = new IshRESTClient(BASE_URL)

        httpClient.request(Method.GET, ContentType.JSON) {
            uri.path = MEMBERSHIP_URL

            uri.query = [
                    access_token: accessToken,
                    organisation_id: orgId,
                    search_role_id: 30,
                    search: email
            ]

            response.success = { resp, result ->
                result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Membership search failed: ${result.response}")
            }
        }
    }

    /**
     * @param accessToken - returned from Login endpoint
     * @param email       - email of student
     * @param firstName   - first name of student
     * @param lastName    - last name of student
     * @param angelId     - angelId of Contact record for this student
     * @return [status, response: [id, email, first_name, last_name, student_number, organisation_id, role_id, external_user_id, external_user_identifier]]
     */
    private membershipCreate(accessToken, email, firstName, lastName, studentNumber, angelId) {
        def httpClient = new IshRESTClient(BASE_URL)

        httpClient.request(Method.POST, ContentType.JSON) {
            uri.path = MEMBERSHIP_URL
            body = [
                    access_token: accessToken,
                    membership: [
                            organisation_id: orgId,
                            email: email,
                            first_name: firstName,
                            last_name: lastName,
                            role_id: 30,
                            active: true, //optional
                            student_number: studentNumber, //optional
                            external_user_id: angelId  //optional
                            //external_user_identifier: uniqueIdentifier //optional
                    ]
            ]

            response.success = { resp, result ->
                result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Membership creation failed: ${result.response}")
            }
        }
    }

    /**
     * @param accessToken - returned from Login endpoint
     * @return list of courses for this organization
     */
    private courseSearch(accessToken, courseCode) {
        def httpClient = new IshRESTClient(BASE_URL)

        httpClient.request(Method.GET, ContentType.JSON) {
            uri.path = COURSE_URL

            uri.query = [
                    access_token: accessToken,
                    organisation_id: orgId,
                    search: courseCode
            ]

            response.success = { resp, result ->
                result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Course search failed: ${result.response}")
            }
        }
    }

    /**
     * @param accessToken       - returned from Login endpoint
     * @param code              - course code
     * @param name              - course name
     * @param qualificationCode - qualification Code for course
     * @return [status, response: [id, code, name, qualification: [id, code, name], organisation_id, external_course_id]]
     */
    private courseCreate(accessToken, code, name, qualificationCode) {
        def httpClient = new IshRESTClient(BASE_URL)

        httpClient.request(Method.POST, ContentType.JSON) {
            uri.path = COURSE_URL
            body = [
                    access_token: accessToken,
                    course: [
                            organisation_id: orgId,
                            code: code,
                            name: name,
                            //qualification_id: "Cloud Assess database ID", // (required or provide ‘qualification_code’ instead)
                            qualification_code: qualificationCode
                    ]
            ]

            response.success = { resp, result ->
                result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Course creation failed: ${result.response}")
            }
        }
    }

    /**
     * @param accessToken - returned from Login endpoint
     * @param courseId    - course id
     * @return list of enrolments of this course
     */
    private enrolmentSearch(accessToken, courseId) {
        def httpClient = new IshRESTClient(BASE_URL)

        httpClient.request(Method.GET, ContentType.JSON) {
            uri.path = COURSE_ENROLMENT_URL

            uri.query = [
                    access_token: accessToken,
                    course_id: courseId,
            ]

            response.success = { resp, result ->
                result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Course enrolment search failed: ${result.response}")
            }
        }
    }

    /**
     * @param accessToken  - returned from Login endpoint
     * @param courseId     - the id of course to which the enrolment is to be added
     * @param membershipId - the id of the student’s membership to which the enrolment relates
     *
     * @return [status, response: [id, course_id, membership_id]]
     */
    private enrolmentCreate(accessToken, courseId, membershipId) {
        def httpClient = new IshRESTClient(BASE_URL)

        httpClient.request(Method.POST, ContentType.JSON) {
            uri.path = COURSE_ENROLMENT_URL
            body = [
                    access_token: accessToken,
                    course_enrolment: [
                            course_id: courseId,
                            membership_id: membershipId,
                            enrol_in_all_units: true
                    ]
            ]

            response.success = { resp, result ->
                result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Course enrolment creation failed: ${result.response}")
            }
        }
    }

    /**
     *
     * @param accessToken - returned from Login endpoint
     * @param from        - records completed on or after this date (API will automatically use the user’s timezone) (Date should be in the format ’23-Jun-2013')
     * @param to          - records completed on or before this date (API will automatically use the user’s timezone) (Date should be in the format ’23-Jul-2013')
     *
     * @return list of assessment records that have been completed (2 = "Competent", 3 = "Not Yet Competent") in the selected organisation.
     */
    private completedUnitRecords(accessToken, from, to) {
        def httpClient = new IshRESTClient(BASE_URL)

        httpClient.request(Method.GET, ContentType.JSON) {
            uri.path = COMPLETED_UNIT_RECORDS_URL
            uri.query = [
                    access_token: accessToken,
                    organisation_id: orgId,
                    search_completed_from: from,
                    search_completed_to: to,
                    search_outcome: 2 // (2 = "Competent", 3 = "Not Yet Competent")

            ]
            response.success = { resp, result ->
                result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Request failed: ${result.response}")
            }
        }
    }
}
