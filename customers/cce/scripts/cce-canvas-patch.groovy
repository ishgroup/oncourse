import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import groovy.json.JsonSlurper


def getAllCourses() {
    def client = new RESTClient(BASE_URL)

    client.headers["Authorization"] = "Bearer ${TEST_AUTH}"
    def results = client.request(Method.GET, ContentType.URLENC) {
        uri.path = "/api/v1/accounts/${ACCOUNT_CODE}/courses"

        response.success = { resp, result ->
            return result
        }
    }
    return responseToJson(results)
}

def getCourse(code) {
    def courses = getAllCourses()
    return courses.find { c ->
        c.course_code == code
    }
}

def getUserByEmail(email) {
    def client = new RESTClient(BASE_URL)

    client.headers["Authorization"] = "Bearer ${TEST_AUTH}"
    client.request(Method.GET, ContentType.URLENC) {
        uri.path = "/api/v1/accounts/${ACCOUNT_CODE}/users"
        uri.query = [
                search_term: email,

        ]

        response.success = { resp, result ->
            return result
        }
        response.failure = { resp, result ->
            return result
        }
    }
}

def createNewUser(fullName, email) {
    def client = new RESTClient(BASE_URL)

    client.headers["Authorization"] = "Bearer ${TEST_AUTH}"
    client.request(Method.POST, ContentType.JSON) {
        uri.path = "/api/v1/accounts/${ACCOUNT_CODE}/users"
        body = [
                user: [
                        name: fullName,
                ],
                pseudonym: [
                        unique_id: email,
                        send_confirmation: false,
                        force_self_registration: true
                ]
        ]

        response.success = { resp, result ->
            println("success")
            return result
        }
        response.failure = { resp, result ->
            print("failure")
            return resp.status
        }
    }
}


def getSectionsByCourse(courseId) {
    def client = new RESTClient(BASE_URL)

    client.headers["Authorization"] = "Bearer ${TEST_AUTH}"
    client.request(Method.GET, ContentType.URLENC) {
        uri.path = "/api/v1/courses/${courseId}/sections"

        response.success = { resp, result ->
            return result
        }
    }
}

def createSection(name, courseId) {
    def client = new RESTClient(BASE_URL)
    client.headers["Authorization"] = "Bearer ${TEST_AUTH}"
    client.request(Method.POST, ContentType.JSON) {
        uri.path = "/api/v1/courses/${courseId}/sections"
        body = [
                course_section: [
                        name: name,
                ]
        ]
        response.success = { resp, result ->
            return result
        }
    }
}

def enrolUser(studentId, sectionId) {
    def client = new RESTClient(BASE_URL)
    client.headers["Authorization"] = "Bearer ${TEST_AUTH}"
    client.request(Method.POST, ContentType.JSON) {
        uri.path = "/api/v1/sections/${sectionId}/enrollments"
        body = [
                enrollment: [
                        user_id: studentId,
                        type: "StudentEnrollment",
                        enrollment_state: "active",
                ]
        ]
        response.success = { resp, result ->
            return result
        }

        response.failure = { resp, result ->
            return resp.status
        }
    }
}

def getAuthToken() {
    def client = new RESTClient(BASE_URL)
    client.request(Method.GET, ContentType.URLENC) {
        uri.path = "/login/oauth2/auth"
        uri.query = [
                client_id: CANVAS_CLIENT_ID,
                response_type: "code",
                redirect_uri: "urn:ietf:wg:oauth:2.0:oob"
        ]

        println(uri.path)
        println(uri)
        response.success = { resp, result ->
            return result
        }
        response.failure = { resp, result ->
            return resp.status
        }
    }
}

def setAuthToken() {
    def client = new RESTClient(BASE_URL)
    client.request(Method.POST, ContentType.URLENC) {
        uri.path = "/login/oauth2/token"
        body = [
                grant_type: "refresh_token",
                client_id: CANVAS_CLIENT_ID,
                client_secret: CANVAS_CLIENT_SECRET,
                refresh_token: REFRESH
        ]
        println(uri)
        response.success = { resp, result ->
            print(responseToJson(result)["access_token"])
        }
        response.failure = { resp, result ->
            return resp.status
        }
    }
}

def responseToJson(resp) {
    def respKeySet = []
    respKeySet.addAll(resp.keySet())
    return new JsonSlurper().parseText(respKeySet)
}

def run(args) {
    def enrolment = args.entity
    BASE_URL = "https://canvas-catalog.sydney.edu.au/"
    TEST_AUTH = "14240~GIHBLoXJIdy8T8CC7bBHjkA0qaWmTtOWrlZVeyvJsonlJXpTI3x1XMXfAp1VvIER"
    ACCOUNT_CODE = "36"

    if (enrolment.courseClass.course.hasTag("Canvas")) {
        if (enrolment.student.contact.email) {

            def student
            def studentResp = responseToJson(getUserByEmail(enrolment.student.contact.email))

            if (!studentResp) {
                student = createNewUser(enrolment.student.contact.fullName, enrolment.student.contact.email)
            } else {
                student = studentResp.first()
            }


            def courses = getAllCourses()
            def course = courses.find { c ->
                c.course_code == "CCE." + enrolment.courseClass.course.code
            }

            getSectionsResp = getSectionsByCourse(course["id"])
            sectionsJson = responseToJson(getSectionsResp)


            section = sectionsJson.find { s->
                s.name == "CCE." + enrolment.courseClass.uniqueCode
            }

            if (!section) {
                section = createSection("CCE." + enrolment.courseClass.uniqueCode, course["id"])
            }

            enrolUser(student["id"], section["id"])

        }
    }
}