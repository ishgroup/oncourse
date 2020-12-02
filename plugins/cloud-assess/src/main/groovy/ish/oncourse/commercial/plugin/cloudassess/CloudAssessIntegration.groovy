/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.cloudassess

import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.common.types.OutcomeStatus
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
@Plugin(type = 7)
class CloudAssessIntegration implements PluginTrait {
    public static final String CLOUDASSESS_API_KEY = "apiKey"

    // key for ish onCourse to be kept confidential
    static final String clientKey = "MTqAhbUoJno8HCHJG5NBDo1L"

    static final String BASE_URL = "https://my.assessapp.com"

    static final String BASE_URL_TEST = "https://stg.assessapp.com"

    static final String MEMBERSHIP_URL = "/api/v2/members"
    static final String COURSE_URL = "/api/v2/intakes"
    static final String QUALIFICATION_URL = "/api/v2/qualifications"
    static final String ENROLMENT_URL = "/api/v2/enrolments"
    static final String UNIT_ENROLMENTS_URL = "/api/v2/unit_enrolments"

    static final DATE_FORMAT_PATTEN = "yyyy-MM-dd"

    def apiKey

    private static Logger logger = LogManager.logger

    CloudAssessIntegration(Map args) {
        loadConfig(args)
        this.apiKey = configuration.getIntegrationProperty(CLOUDASSESS_API_KEY).value
    }

    private RESTClient client() {
        RESTClient httpClient = new RESTClient(BASE_URL)
        httpClient.headers["Api-Key"] = apiKey
        httpClient.headers["Client-Key"] = clientKey
        httpClient
    }

    protected enrol(Enrolment enrolment, String code) {

        String courseCode = code ?: enrolment.courseClass.course.code

        Long courseId = courseSearch(courseCode)

        if (courseId) {
            String email = enrolment.student.contact.email
            Long memberId = memberSearch(email)
            memberId = memberId ?: memberCreate(
                    email,
                    enrolment.student.contact.firstName,
                    enrolment.student.contact.lastName,
                    enrolment.student.studentNumber.toString(),
                    enrolment.student.contact.id)

            enrolmentCreate(courseId, memberId, enrolment.id)
        }
    }

    protected course(code, courseName, qualification) {

        Long courseId = courseSearch(code)

        if (!courseId) {
            courseCreate(code, courseName, qualification)
        }
    }

    protected void outcomes(Date since) {

        String fromDate = (since ?: Calendar.getInstance().getTime() - 1).format(DATE_FORMAT_PATTEN)
        String toDate = Calendar.getInstance().getTime().format(DATE_FORMAT_PATTEN)

        List enrolmentUnits = completedUnitRecords(fromDate, toDate)

        updatedOnCourseOutcomes(enrolmentUnits)
    }

    protected static updatedOnCourseOutcomes(List enrolmentUnits) {
        def updatedOutcomes = []
        enrolmentUnits.each { enrolmentUnit ->
            Expression studentQualifier
            if (enrolmentUnit.member.external_id) {
                studentQualifier = Outcome.ENROLMENT
                        .dot(Enrolment.STUDENT)
                        .dot(Student.CONTACT)
                        .dot(Contact.ID)
                        .eq(enrolmentUnit.member.external_id)
            } else if (enrolmentUnit.member.learner_code) {
                studentQualifier = Outcome.ENROLMENT
                        .dot(Enrolment.STUDENT)
                        .dot(Student.STUDENT_NUMBER)
                        .eq(Long.valueOf(enrolmentUnit.member.learner_code as String))
            } else if (enrolmentUnit.member.email) {
                studentQualifier = Outcome.ENROLMENT
                        .dot(Enrolment.STUDENT)
                        .dot(Student.CONTACT)
                        .dot(Contact.EMAIL)
                        .eq(enrolmentUnit.member.email)
            } else {
                throw new IllegalStateException("The enrolment unit has wrong member assigned: $enrolmentUnit")
            }

            def outcomes = ObjectSelect.query(Outcome)
                    .where(studentQualifier)
                    .and(Outcome.MODULE.dot(ish.oncourse.server.cayenne.Module.NATIONAL_CODE).eq(enrolmentUnit.unit.code as String))
                    .select(cayenneService.newContext)

            outcomes.each { o ->

                //'C' = "Competent", 'CN' = "Not Competent"
                def outcomeStatus = enrolmentUnit.outcome.acronym == 'C' ? OutcomeStatus.STATUS_ASSESSABLE_PASS : (enrolmentUnit.outcome.acronym == 'NC' ? OutcomeStatus.STATUS_ASSESSABLE_FAIL : null)
                if (outcomeStatus && o.status != outcomeStatus) {
                    o.status = outcomeStatus
                    updatedOutcomes << o
                }
            }

        }

        updatedOutcomes
    }


    /**
     * @return id
     */
    private Long memberSearch(email) {
        Long id = null
        client().request(Method.GET, ContentType.JSON) {
            uri.path = MEMBERSHIP_URL

            uri.query = [
                    email: email,
            ]

            response.success = { resp, result ->
                id = result.find{ member -> member.role == 'learner'}?.id
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Membership search failed: ${result.response}")
            }
        }
        return id
    }

    /**
     * @param email       - email of student
     * @param firstName   - first name of student
     * @param lastName    - last name of student
     * @param angelId     - angelId of Contact record for this student
     * @return cloud assess member id
     */
    private Long memberCreate(email, firstName, lastName, studentNumber, angelId) {
        Long id = null
        client().request(Method.POST, ContentType.JSON) {
            uri.path = MEMBERSHIP_URL
            body = [
                    member: [
                            email: email,
                            first_name: firstName,
                            last_name: lastName,
                            role: 'learner',
                            active: true, //optional
                            learner_code: studentNumber, //optional
                            external_id: angelId  //optional
                    ]
            ]

            response.success = { resp, result ->
                id = result.id
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Membership creation failed: ${result.response}")
            }
        }
        return id
    }

    /**
     * @return list of courses for this organization
     */
    private Long courseSearch(courseCode) {
        Long id = null
        client().request(Method.GET, ContentType.JSON) {
            uri.path = COURSE_URL

            uri.query = [
                    search: courseCode
            ]

            response.success = { resp, result ->
                id = result.find { course -> course.code == courseCode}?.id
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Course search failed: ${result.response}")
            }
        }
        return id
    }

    /**
     * @param code              - course code
     * @param name              - course name
     * @param qualificationCode - qualification Code for course
     * @return id
     */
    private courseCreate(String code, String name, String qualificationCode) {
        Long qualificationId =  qualificationSearch(qualificationCode)
        client().request(Method.POST, ContentType.JSON) {
            uri.path = COURSE_URL
            body = [
                    intake: [
                            code: code,
                            title: name,
                            qualification_id: qualificationId,
                            active: true,
                    ]
            ]

            response.success = { resp, result ->
                result.id
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Course creation failed: ${result.response}")
            }
        }
    }

    /**
     * @return id
     */
    private Long qualificationSearch(String qualificationCode) {
        Long id = null
        client().request(Method.GET, ContentType.JSON) {
            uri.path = QUALIFICATION_URL

            uri.query = [
                    search: qualificationCode
            ]

            response.success = { resp, result ->
                id = result.find { qualification -> qualification.code == qualificationCode}?.id
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Qualification search failed: ${result.response}")
            }
        }
        return id
    }

    /**
     * @param accessToken  - returned from Login endpoint
     * @param courseId     - the id of course to which the enrolment is to be added
     * @param membershipId - the id of the student’s membership to which the enrolment relates
     *
     * @return [status, response: [id, course_id, membership_id]]
     */
    private void enrolmentCreate(Long courseId, Long memberId, Long angelId) {
        client().request(Method.POST, ContentType.JSON) {
            uri.path = ENROLMENT_URL
            body = [
                    enrolment: [
                            intake_id: courseId,
                            member_id: memberId,
                            active: true,
                            external_id: angelId
                    ]
            ]

            response.success = { resp, result ->
                return
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
    private List completedUnitRecords( from, to) {
        List units = []
        client().request(Method.GET, ContentType.JSON) {
            uri.path = UNIT_ENROLMENTS_URL
            uri.query = [
                    completed_from: from,
                    completed_to: to,
            ]

            response.success = { resp, result ->
                units = result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Request failed: ${result.response}")
            }
        }
        return units
    }
}
