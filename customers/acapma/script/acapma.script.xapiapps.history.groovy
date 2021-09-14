/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import groovy.transform.CompileDynamic
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseException
import groovyx.net.http.ContentType
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Student
import org.apache.cayenne.query.ObjectSelect

Xapiapps xapiapps = new Xapiapps(url, publicToken, secretToken, groupStoreKey)

def changes = xapiapps.history()

if (changes && !changes.empty ) {
    changes.each { result ->
        def task = result.document
        String studentUUID = task.roles?.target?.key // 2e37ee8f-7056-4270-89a0-bc9d3e826ebf
        String pathUUID = task.path?.key // 2c9d9c0b-536c-4125-8c14-db5fc9f4d431*path
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

def createAssessment(String email, String courseName, String assessment) {
    List<Student> students = ObjectSelect.query(Student)
            .where(Student.CONTACT.dot(Contact.EMAIL).eq(email))
            .select(context)
    if (students.empty) {
        error("can not find student with email: $email")
        return
    }
    if (students.size() > 1) {
        error("there are more than one student withemail: $email")
        return
    }
    Student student = students[0]
    //todo
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
        // note that we need to fetch the history in small
        // batch sizes
    
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