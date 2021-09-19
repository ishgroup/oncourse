/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseException
import groovyx.net.http.ContentType
import ish.common.types.EnrolmentStatus
import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.AssessmentSubmission
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Student
import ish.statistics.EnrolmentStats
import org.apache.cayenne.query.ObjectSelect

Xapiapps xapiapps = new Xapiapps(url, publicToken, secretToken, groupStoreKey)

def changes = xapiapps.history()

if (changes && !changes.empty ) {
    changes.each { result ->
        def task = result.document
        String studentUUID = task.roles?.target?.key 
        String pathUUID = task.path?.key
        if (studentUUID || pathUUID) {
            def user = xapiapps.getUser(studentUUID) 
            def path = xapiapps.path(pathUUID)
            String courseName = path.name
            String email = user.email
            String assessmentName = task.config?.title // Weclome Module Placeholder
            createAssessment(email, courseName, assessmentName)
        }
    }
}

def error(String errorMessage) {
    message {
        to preference.email.admin
        subject 'Xapiapps history check failed'
        content errorMessage
    }
}

def createAssessment(String email, String courseName, String assessmentName) {
    List<Student> students = ObjectSelect.query(Student)
            .where(Student.CONTACT.dot(Contact.EMAIL).eq(email))
            .select(context)
    if (students.empty) {
        return error("can not find student with email: $email")

    }
    if (students.size() > 1) {
        return error("there are more than one student withemail: $email")

    }
    Student student = students[0]
    Enrolment enrolment = student.enrolments
            .find { e -> e.status == EnrolmentStatus.SUCCESS && e.courseClass.course.name.equalsIgnoreCase(courseName)}
    if (!enrolment) {
        return error("student: $email has no active enrolment for $courseName")

    }

    AssessmentClass assessmentClass = enrolment.courseClass.assessmentClasses.find { it.assessment.name == assessmentName }
    if (!assessmentClass) {
        return error("Class $enrolment.courseClass.course.name ($enrolment.courseClass.uniqueCode) has not $assessmentName assessment task added, student: $email")
    }

    AssessmentSubmission submission = enrolment.assessmentSubmissions.find {it.assessmentClass == assessmentClass }
    Date lastDay = new Date().clearTime() -1
    if (!submission) {
        submission = context.newObject(AssessmentSubmission)
        submission.assessmentClass = assessmentClass
        submission.enrolment = enrolment
        submission.createdOn = lastDay
        submission.modifiedOn = lastDay
    }
    submission.submittedOn = lastDay
    submission.markedOn = lastDay
    submission.grade = new BigDecimal(100)
    context.commitChanges()
}


class Xapiapps {
    
    String url
    String publicToken
    String secretToken
    String groupStoreKey
    
    Xapiapps(String url, String publicToken, String secretToken, String groupStoreKey) {
        this.url = url
        this.publicToken = publicToken
        this.secretToken = secretToken
        this.groupStoreKey = groupStoreKey
    }
    
    def getUser(String uuid) {
        def httpClient = new RESTClient(url + "/api2/r/search")
        httpClient.request(Method.POST, ContentType.JSON) {
            body = [
                    ipubtoken: publicToken,
                    itoken: secretToken,
                    indexname: "Person2",
                    query: "key: " + uuid
            ]
    
            response.success = { resp, result ->
                return result.results[0].document
            }
            response.failure = { resp, result ->
                logger.error(result)
            }
        }
    }
    
    def history() {
    
        def endOfPreviousDay = new Date().clearTime() 
        def startOfPreviousDay = endOfPreviousDay - 1
        def httpClient = new RESTClient(url + "api2/r/search")
        httpClient.request(Method.POST, ContentType.JSON) {
            body = [
                    ipubtoken: publicToken,
                    itoken: secretToken,
                    indexname: "Task",
                    query: "DOC_OPSTATUS:complete AND learnergroups:${groupStoreKey} AND (DOC_TIMING_COMPLETEAT_TS >= ${startOfPreviousDay.getTime()/1000} AND DOC_TIMING_COMPLETEAT_TS < ${endOfPreviousDay.getTime()/1000})".toString()
            ]
    
            response.success = { resp, result ->
                return result.results
            }
            response.failure = { resp, result ->
                logger.error(resp)
            }
        }
    }
    
    def path(String uuid) {
        def httpClient = new RESTClient(url + "api2/r/search")
        httpClient.request(Method.POST, ContentType.JSON) {
            body = [
                    ipubtoken: publicToken,
                    itoken: secretToken,
                    indexname: "Path",
                    query: "key: " + uuid
            ]
    
            response.success = { resp, result ->
                return result.results[0].document
            }
            response.failure = { resp, result ->
                logger.error(result)
            }
        }
    }
}