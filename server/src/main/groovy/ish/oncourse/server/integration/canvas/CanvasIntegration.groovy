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

package ish.oncourse.server.integration.canvas

import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.oncourse.server.api.v1.service.impl.IntegrationApiImpl
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.IntegrationProperty
import ish.oncourse.server.integration.GetProps
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.OnSave
import ish.oncourse.server.integration.PluginTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
@Plugin(type = 8)
class CanvasIntegration implements PluginTrait {
    public static final String CANVAS_BASE_URL_KEY = "baseUrl"
    public static final String CANVAS_CLIENT_TOKEN_KEY = "clientToken"
    public static final String CANVAS_CLIENT_SECRET_KEY = "clientSecret"
    public static final String CANVAS_ACCOUNT_ID_KEY = "accountId"
    public static final String CANVAS_REFRESH_TOKEN = "refreshToken"
    public static final String CANVAS_REDIRECT_URL = "redirectUrl"


    String baseUrl
    String clientToken
    String clientSecret
    String accountId
    String authHeader
    String refreshToken

    private static Logger logger = LogManager.logger

    CanvasIntegration(Map args) {
        loadConfig(args)

        this.baseUrl = configuration.getIntegrationProperty(CANVAS_BASE_URL_KEY).value
        this.clientToken = configuration.getIntegrationProperty(CANVAS_CLIENT_TOKEN_KEY).value
        this.clientSecret = configuration.getIntegrationProperty(CANVAS_CLIENT_SECRET_KEY).value
        this.accountId =configuration.getIntegrationProperty(CANVAS_ACCOUNT_ID_KEY).value
        this.refreshToken = configuration.getIntegrationProperty(CANVAS_REFRESH_TOKEN).value
    }

    /**
     * A new auth header will need to be generated every hour or so. So only call this function if you have a very long running script
     * using this CanvasIntegration object
     *
     * @return the access token for used as Bearer authentication
     */
    def initAuthHeader() {
        def client = new RESTClient(baseUrl)
        client.request(Method.POST, ContentType.URLENC) {
            uri.path = "/login/oauth2/token"
            body = [
                    grant_type: "refresh_token",
                    client_id: clientToken,
                    client_secret: clientSecret,
                    refresh_token: refreshToken,
            ]
            response.success = { resp, result ->
                authHeader =  responseToJson(result)["access_token"]
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to refresh access token  ${resp.getStatusLine()}, ${result}")
            }
        }

    }

    /**
     * When multiple objects are returned from the Canvas API, all objects are returned as a string in the keySet of a hash map. The hash map has no values.
     * This method transforms the keySet() of a map to a json object for ease of use.
     *
     * @param resp Map return from Canvas API
     * @return response as JSON
     */
    protected responseToJson(Map resp) {
        def respKeySet = []
        respKeySet.addAll(resp.keySet())
        return new JsonSlurper().parseText(respKeySet)
    }

    protected getUserByEmail(String email) {
        def client = new RESTClient(baseUrl)

        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.GET, ContentType.URLENC) {
            uri.path = "/api/v1/accounts/${accountId}/users"
            uri.query = [
                    search_term: email,
                    per_page: '100000',
            ]

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to retreive user by email:${email} ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Get all users from Canvas
     *
     * @return users
     */
    protected List getAllUsers() {
        getAllWithoutPagination({ nextPageUrl -> getUsersWithPagination(nextPageUrl) })
    }

    /**
     * Get users from Canvas by url
     *
     * @param nextPageUrl url with params 'per_page' and 'page'
     * @return users
     */
    protected getUsersWithPagination(nextPageUrl) {
        def url = nextPageUrl != null ? nextPageUrl : (baseUrl + "/api/v1/accounts/${accountId}/users?page=1&per_page=100")
        def client = new RESTClient(url)

        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.GET, ContentType.URLENC) {
            response.success = { resp, result ->
                return [ "resp": resp, "result": result]
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to retreive users by account id:${accountId} ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Creates a new student user in Canvas from the students full name and email
     *
     * @param fullName full name of the onCourse student
     * @param email email of the onCourse student
     * @return created student object
     */
    def createNewUser(String fullName, String email, String contactId, int authenticationProviderId, String password, Map studentAttributes) {
        def client = new RESTClient(baseUrl)
        def pseudonymProperties = [
                unique_id: email,
                send_confirmation: false,
                force_self_registration: true,
                sis_user_id: contactId
        ]
        if(authenticationProviderId != 0)
            pseudonymProperties.put("authentication_provider_id", authenticationProviderId)
        if(password)
            pseudonymProperties.put("password", password)

        def parameters = [
                user: [
                        name: fullName,
                        skip_registration: password != null
                ],
                pseudonym: pseudonymProperties
        ]
        parameters = mergeMaps(parameters, studentAttributes)

        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/accounts/${accountId}/users"
            body = parameters

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to created a new user name:${fullName} email:${email} ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Get list unsuspended user from Canvas
     *
     * @return list user objects
     */
    protected List getUnsuspendedUsers() {
        List users = getAllUsers()
        List unsuspendedUsers = new ArrayList<>()
        users.each { it ->
            List userLogins = getAllUserLogins(it["id"])
            if (userLogins["workflow_state"].contains("active")) {
                unsuspendedUsers.add(it)
            }
        }
        return unsuspendedUsers
    }

    /**
     * Get all user's enrolments from Canvas
     *
     * @param user id from Canvas
     * @return enrolments
     */
    protected List getAllUserEnrolments(userId) {
        getAllWithoutPagination({ nextPageUrl -> getEnrolmentsWithPagination(nextPageUrl, userId) })
    }

    /**
     * Get user's enrolments from Canvas by url
     *
     * @param nextPageUrl url with params 'per_page' and 'page'
     * @param user id from Canvas
     * @return enrolments
     */
    protected getEnrolmentsWithPagination(nextPageUrl, userId) {
        def url = nextPageUrl != null ? nextPageUrl : (baseUrl + "/api/v1/users/${userId}/enrollments?page=1&per_page=100")
        def client = new RESTClient(url)
        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.GET, ContentType.URLENC) {
            response.success = { resp, result ->
                return [ "resp": resp, "result": result]
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to retreive enrolments by user id:${userId} ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Check whether user is suspended and then unsuspend it in Canvas
     *
     * @param user id from Canvas
     */
    protected checkIfUserSuspendedAndUnsuspend(userId) {
        List userLogins = getAllUserLogins(userId)
        if (userLogins["workflow_state"].contains("suspended")) {
            unsuspendUser(userId)
        }
    }

    /**
     * Get list of user's logins for the given account.
     *
     * @param user id from Canvas
     */
    protected getAllUserLogins(userId) {
        getAllWithoutPagination({ nextPageUrl -> getUserLoginsWithPagination(nextPageUrl, userId) })
    }

    /**
     * Get list of user's logins for the given account by url
     *
     * @param nextPageUrl url with params 'per_page' and 'page'
     * @param user id from Canvas
     */
    protected getUserLoginsWithPagination(nextPageUrl, userId) {
        def url = nextPageUrl != null ? nextPageUrl : (baseUrl + "/api/v1/users/${userId}/logins?page=1&per_page=100")
        def client = new RESTClient(url)
        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.GET, ContentType.URLENC) {

            response.success = { resp, result ->
                return [ "resp": resp, "result": result]
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to retreive logins by user id:${userId} ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Suspend all logins for this user that the calling user has permission to
     *
     * @param user id from Canvas
     */
    protected suspendUser(userId) {
        editUserEvent(userId, "suspend")
    }

    /**
     * Unsuspend all logins for this user that the calling user has permission to
     *
     * @param user id from Canvas
     */
    protected unsuspendUser(userId) {
        editUserEvent(userId, "unsuspend")
    }

    /**
     * Suspend or unsuspend all logins for this user that the calling user has permission to
     *
     * @param user id from Canvas
     * @param event (allowed values: "suspend", "unsuspend")
     */
    private editUserEvent(userId, event) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.PUT, ContentType.JSON) {
            uri.path = "/api/v1/users/${userId}"
            body = [
                    user: [
                            event: event
                    ]
            ]

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to edit user, user id:${userId} event:${event} ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Get all courses from Canvas
     *
     * @return collection of courses
     */
    def getAllCourses() {
        def client = new RESTClient(baseUrl)

        client.headers["Authorization"] = "Bearer ${authHeader}"
        def result = client.request(Method.GET, ContentType.URLENC) {
            uri.path = "/api/v1/accounts/${accountId}/courses"
            //ignore pagination
            uri.query = [
                    per_page: '10000',
            ]
            response.success = { resp, result ->
                return result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to retreive all courses ${resp.getStatusLine()}, ${result}")
            }
        }

        return responseToJson(result)
    }

    /**
     * Get a course from Canvas by the course code
     *
     * @return the course object the matching course code
     */
    def getCourse(String code) {
        def client = new RESTClient(baseUrl)

        client.headers["Authorization"] = "Bearer ${authHeader}"
        def result = client.request(Method.GET, ContentType.URLENC) {
            uri.path = "/api/v1/accounts/${accountId}/courses"
            uri.query = [
                    search_by: "course",
                    search_term: code
            ]
            response.success = { resp, result ->
                return result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to retreive all courses ${resp.getStatusLine()}, ${result}")
            }
        }

        return responseToJson(result)
    }

    /**
     * Create a new course in Canvas from the blueprint specified
     *
     * @param courseBlueprint course code with blueprint=true in Canvas
     * @param courseCode code of course to create Course in Canvas
     * @param courseName name of course to create Course in Canvas
     * @param courseId id of course to create Course in Canvas
     * @return created course object from the blueprint specified
     */
    def createNewCourseFromBlueprint(String courseBlueprint, String courseCode, String courseName, String courseId) {
        List coursesByBlueprintCode = getCourse(courseBlueprint) as List
        def blueprintCourses = coursesByBlueprintCode.findAll { it["blueprint"] == true}
        if (blueprintCourses.size() == 0) {
            throw new IllegalArgumentException("Illegal state: There are no blueprint courses with specified code ${courseBlueprint}, when get courses using account_id ${accountId}")
        }
        if (blueprintCourses.size() > 1) {
            throw new IllegalArgumentException("Illegal state: There are find more that one blueprint course for specified course code: ${courseBlueprint}. " +
                    "Please, specify more unique course code.")
        }
        def course = createNewCourse(courseCode, courseName, courseId)
        def resultOfUpdate = updateAssociatedCourses(blueprintCourses["id"][0], blueprintCourses["account_id"][0], List.of(course["id"]), course["account_id"])
        if (resultOfUpdate["success"] == true) {
            migrateFromBlueprintCourse(blueprintCourses["id"][0])
            return getCourse(courseCode)
        } else {
//            If delete course in Canvas, sis_course_id will remain in the Canvas and user can't create new Course with same sis_course_id (course.id in Angel) in Canvas.
//            Decided to leave the course even if there were errors during migration from blueprint. The course can be linked and synchronized manually in Canvas.
//            deleteCourse(course["id"] as int)
            throw new IllegalStateException("Failed to update associated courses, course code: ${courseCode}, blueprint course code: ${blueprintCourses["id"][0]}")
        }
    }

    /**
     * Create a new course in Canvas
     *
     * @param courseCode code of course to create Course in Canvas
     * @param courseName name of course to create Course in Canvas
     * @param courseId id of course to create Course in Canvas
     * @return created course object
     */
    def createNewCourse(String courseCode, String courseName, String courseId) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/accounts/${accountId}/courses"
            body = [
                    course: [
                            name: courseName,
                            course_code: courseCode,
                            sis_course_id: courseId,
                    ]
            ]
            response.success = { resp, result ->
                return result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to create course, course code: ${courseCode}, course name: ${courseName}, course id: ${courseId} ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Delete course in Canvas
     *
     * @param courseId id of course to delete Course in Canvas
     * @return delete status response
     */
    def deleteCourse(int courseId) {
        def client = new CanvasRESTClient(baseUrl)
        client.headers["Authorization"] = "Bearer ${authHeader}"
        try {
            client.delete(
                    path: "/api/v1/courses/${courseId}",
                    body: [
                            event: 'delete'
                    ],
                    requestContentType: 'application/json'
            )
        } catch (HttpResponseException e) {
            throw new IllegalStateException("Failed to delete course, course id: ${courseId}. ${e.getMessage()}")
        }
    }

    /**
     * Update associated courses to blueprint course
     *
     * @param blueprintCourseId course id of blueprint from Canvas
     * @param courseIdsToAdd courses ids from Canvas to add as associated courses
     * @return success status
     */
    def updateAssociatedCourses(int blueprintCourseId, int blueprintCourseAccountId, List courseIdsToAdd, courseAccountIdsToAdd) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.PUT, ContentType.JSON) {
            uri.path = "/api/v1/courses/${blueprintCourseId}/blueprint_templates/default/update_associations"
            body = [
                    course_ids_to_add: courseIdsToAdd,
            ]
            response.success = { resp, result ->
                return result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to update associated courses, blueprint course id: ${blueprintCourseId}, course ids ${courseIdsToAdd} ${resp.getStatusLine()}, ${result}. " +
                        "Blueprint course account_id = ${blueprintCourseAccountId}, account_ids of the added courses = ${courseAccountIdsToAdd}. Course account should be same or sub-account of blueprint course.")
            }
        }
    }

    /**
     * Begins a migration to push recently updated content to all associated courses
     *
     * @param blueprintCourseId course id of blueprint from Canvas
     * @return BlueprintMigration object
     */
    def migrateFromBlueprintCourse(int blueprintCourseId) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/courses/${blueprintCourseId}/blueprint_templates/default/migrations"
            response.success = { resp, result ->
                return result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to migrate associated courses, blueprint course id: ${blueprintCourseId} ${resp.getStatusLine()}, ${result}")
            }
        }
    }
    /**
     * Get sections for a Canvas course by url
     *
     * @param courseId course to retreive sections from
     * @param nextPageUrl url with params 'per_page' and 'page'
     * @return collection of sections
     */
    def getSectionsByCourseWithPagination(nextPageUrl, courseId) {
        def url = nextPageUrl != null ? nextPageUrl : (baseUrl + "/api/v1/courses/${courseId}/sections?page=1&per_page=100")
        def client = new RESTClient(url)

        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.GET, ContentType.URLENC) {
            response.success = { resp, result ->
                return [ "resp": resp, "result": result]
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get sections for course, course id:${courseId} ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Get all sections for a Canvas course
     *
     * @param courseId course to retreive sections from
     * @return collection of sections
     */
    def getAllSectionsByCourse(courseId) {
        getAllWithoutPagination({ nextPageUrl -> getSectionsByCourseWithPagination(nextPageUrl, courseId) })
    }

    /**
     * get a section from canvas by the class unique code
     *
     * @param uniqueCode oncourse class code
     * @param courseId canvas course id
     * @return
     */
    def getSection(uniqueCode, courseId) {
        def sections = getAllSectionsByCourse(courseId)

        return sections.find { s ->
            s.name == uniqueCode
        }
    }

    /**
     * A Canvas section is equivalent to an onCourse CourseClass
     *
     * @param code section unique code
     * @param courseId Canvas course id
     * @return section object
     */
    def createSection(code, courseId, courseClassId) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/courses/${courseId}/sections"
            body = [
                    course_section: [
                            name: code,
                            sis_section_id: courseClassId
                    ]
            ]
            response.success = { resp, result ->
                return result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to create section, course id: ${courseId}, section name: ${code}, course class id: ${courseClassId} ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Create a new enrolment with the student and section ids
     *
     * @param studentId
     * @param sectionId
     * @return enrolment object
     */
    def enrolUser(studentId, sectionId) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/sections/${sectionId}/enrollments"
            body = [
                    enrollment: [
                            user_id: studentId,
                            type: "StudentEnrollment",
                            enrollment_state: "active"
                    ]
            ]
            response.success = { resp, result ->
                return result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to enrol student: ${studentId} into section: ${sectionId} ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    /**
     * Enrol teachers into the Canvas course
     *
     * @param List<Contact> tutors
     * @param courseId from Canvas
     * @return enroled teachers
     */
    def enrolTeachers(List<Contact> tutors, courseId) {
        List enroledTeachers = new ArrayList<>()
        tutors.each { tutor ->
            Map userResp = getUserByEmail(tutor.email) as Map
            List teachers = responseToJson(userResp) as List
            teachers = teachers.findAll {tutor.email.equals(it["login_id"])} as List
            if (teachers.size() > 1) {
                logger.warn("More than one teacher found with this email: ${tutor.email} and name: ${tutor.name}. Enrol teachers to the course is impossible")
                return
            }
            if (teachers.size() == 1) {
                enrolTeacherForCourse(teachers["id"][0], courseId)
                enroledTeachers.add(teachers)
            }
        }
        return enroledTeachers
    }

    /**
     * Create a new enrolment with the teacher and course id
     *
     * @param teacherId from Canvas
     * @param courseId from Canvas
     * @return enrolment object
     */
    def enrolTeacherForCourse(teachertId, courseId) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/courses/${courseId}/enrollments"
            body = [
                    enrollment: [
                            user_id: teachertId,
                            type: "TeacherEnrollment",
                            enrollment_state: "active"
                    ]
            ]
            response.success = { resp, result ->
                return result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to enrol teacher: ${teachertId} into course: ${courseId} ${resp.getStatusLine()}, ${result}")
            }
        }
    }

    protected List getAllWithoutPagination(getWithPagination) {
        def ResponseAndResult = getWithPagination(null)
        HttpResponseDecorator response = ResponseAndResult["resp"] as HttpResponseDecorator
        List allObjects = responseToJson(ResponseAndResult["result"] as Map) as List
        def nextPage = response.getHeaders('Link')[0].elements.find { it.parameters.find {it.value == 'next'}}
        while (nextPage) {
            ResponseAndResult = getWithPagination((nextPage.name + "=" + nextPage.value).replaceAll("<|>", ""))
            response = ResponseAndResult["resp"] as HttpResponseDecorator
            allObjects.addAll(responseToJson(ResponseAndResult["result"] as Map) as List)
            nextPage = response.getHeaders('Link')[0].elements.find { it.parameters.find {it.value == 'next'}}
        }
        return allObjects
    }

    @OnSave
    static void onSave(IntegrationConfiguration configuration, Map<String, String> props) {

        String baseUrl = props[CANVAS_BASE_URL_KEY].trim()
        if (!baseUrl.startsWith("https://")) {
            baseUrl = "https://" + baseUrl
        }

        configuration.setProperty(CANVAS_BASE_URL_KEY,baseUrl)
        configuration.setProperty(CANVAS_CLIENT_TOKEN_KEY,props[CANVAS_CLIENT_TOKEN_KEY])
        configuration.setProperty(CANVAS_CLIENT_SECRET_KEY,props[CANVAS_CLIENT_SECRET_KEY])
        configuration.setProperty(CANVAS_ACCOUNT_ID_KEY,props[CANVAS_ACCOUNT_ID_KEY])

        new RESTClient(baseUrl).request(Method.POST, ContentType.URLENC) {
            headers.Accept = 'application/json'

            uri.path = '/login/oauth2/token'
            body = [
                    grant_type: "authorization_code",
                    client_id: props[CANVAS_CLIENT_TOKEN_KEY],
                    client_secret: props[CANVAS_CLIENT_SECRET_KEY],
                    redirect_uri: props[CANVAS_REDIRECT_URL],
                    code: props[(IntegrationApiImpl.VERIFICATION_CODE)]
            ]
            response.success = { resp, Map data ->
                try {
                    String token = new JsonSlurper().parseText(data.keySet()[0])['refresh_token']
                    configuration.setProperty(CANVAS_REFRESH_TOKEN, token)
                } catch (JsonException ignore) {
                    logger.error(data.toString())
                    throw new RuntimeException(data.toString())
                }

            }
            response.failure = { HttpResponseDecorator resp  ->
                String error = resp.responseBase.entity.content.getText("UTF-8")
                logger.error(error)
                throw new RuntimeException(error)
            }
        }
    }

    @GetProps
    static List<IntegrationProperty> getProps(IntegrationConfiguration configuration) {
        return [configuration.getIntegrationProperty(CANVAS_BASE_URL_KEY),
                configuration.getIntegrationProperty(CANVAS_CLIENT_TOKEN_KEY),
                configuration.getIntegrationProperty(CANVAS_CLIENT_SECRET_KEY),
                configuration.getIntegrationProperty(CANVAS_ACCOUNT_ID_KEY)]
    }

    def mergeMaps(Map lhs, Map rhs) {
        rhs.each { k, v ->
            lhs[k] = (lhs[k] in Map ? mergeMaps(lhs[k], v) : v)
        }
        return lhs
    }

}
