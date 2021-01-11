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

import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.oncourse.server.api.v1.service.impl.IntegrationApiImpl
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
    public static final String CANVAS_REDIRECT_URL = "rediretcUrl"


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
                throw new IllegalStateException("Failed to refresh access token  ${resp.getStatusLine()}")
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
            ]

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to retreive user by email:${email} ${resp.getStatusLine()}")
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
    def createNewUser(String fullName, String email) {
        def client = new RESTClient(baseUrl)

        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/accounts/${accountId}/users"
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
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to created a new user name:${fullName} email:${email} ${resp.getStatusLine()}")
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
                throw new IllegalStateException("Failed to retreive all courses ${resp.getStatusLine()}")
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

        // canvas doesn't have an API call to get a course by code so we need to do it in memory
        def courses = getAllCourses()

        return courses.find { c ->
            c.course_code == code
        }

    }

    /**
     * Get all sections for a Canvas course
     *
     * @param courseId course to retreive sections from
     * @return collection of sections
     */
    def getSectionsByCourse(courseId) {
        def client = new RESTClient(baseUrl)

        client.headers["Authorization"] = "Bearer ${authHeader}"
        def result = client.request(Method.GET, ContentType.URLENC) {
            uri.path = "/api/v1/courses/${courseId}/sections"
            //ignore pagination
            uri.query = [
                    per_page: '10000',
            ]
            response.success = { resp, result ->
                return result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get sections for course, course id:${courseId} ${resp.getStatusLine()}")
            }
        }
        return responseToJson(result)
    }

    /**
     * get a section from canvas by the class unique code
     *
     * @param uniqueCode oncourse class code
     * @param courseId canvas course id
     * @return
     */
    def getSection(uniqueCode, courseId) {
        def sections = getSectionsByCourse(courseId)

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
    def createSection(code, courseId) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "Bearer ${authHeader}"
        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/courses/${courseId}/sections"
            body = [
                    course_section: [
                            name: code
                    ]
            ]
            response.success = { resp, result ->
                return result
            }

            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to create section, course id: ${courseId}, section name: ${code} ${resp.getStatusLine()}")
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
                throw new IllegalStateException("Failed to enrol student: ${studentId} into section: ${sectionId} ${resp.getStatusLine()}")
            }
        }
    }

    @OnSave
    static void onSave(IntegrationConfiguration configuration, Map<String, String> props) {
        configuration.setProperty(CANVAS_BASE_URL_KEY,props[CANVAS_BASE_URL_KEY])
        configuration.setProperty(CANVAS_CLIENT_TOKEN_KEY,props[CANVAS_CLIENT_TOKEN_KEY])
        configuration.setProperty(CANVAS_CLIENT_SECRET_KEY,props[CANVAS_CLIENT_SECRET_KEY])
        configuration.setProperty(CANVAS_ACCOUNT_ID_KEY,props[CANVAS_ACCOUNT_ID_KEY])

        new RESTClient("https://" + props[CANVAS_BASE_URL_KEY]).request(Method.POST, ContentType.URLENC) {
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
                String token =  new JsonSlurper().parseText(data.keySet()[0])['refresh_token']
                configuration.setProperty(CANVAS_REFRESH_TOKEN, token)
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

}
