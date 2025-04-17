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

package ish.oncourse.server.integration.learndash

import groovy.json.JsonBuilder
import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.common.types.IntegrationType
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.util.SecurityUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
@Plugin(type= IntegrationType.LEARN_DASH)
class LearnDashIntegration implements PluginTrait {
    public static final String LEARNDASH_LOGIN = "userLogin"
    public static final String LEARNDASH_PASS = "userPassword"
    public static final String LEARNDASH_URL = "baseUrl"

    String userLogin
    String userPassword
    String baseUrl

    private static Logger logger = LogManager.logger

    LearnDashIntegration(Map args) {
        loadConfig(args)

        this.userLogin = configuration.getIntegrationProperty(LEARNDASH_LOGIN).value
        this.userPassword = configuration.getIntegrationProperty(LEARNDASH_PASS).value
        this.baseUrl = configuration.getIntegrationProperty(LEARNDASH_URL).value
    }

    Closure failHandler = { resp, result ->
        String message = "Can notcreate user: ${resp.statusLine.toString()}  \n"
        if (result) {
            message += "response body: ${new JsonBuilder(result).toPrettyString()}"
        }
        logger.error(message)
        throw new IllegalArgumentException(message)
    }


    String getAuthHeader() {
        return  "Basic ${"$userLogin:$userPassword".bytes.encodeBase64().toString()}"
    }


    protected String getCourseId(String code) {
        RESTClient httpClient = new RESTClient(baseUrl)
        httpClient.request(Method.GET, ContentType.JSON) {
            uri.path = "/wp-json/ldlms/v1/sfwd-courses"
            headers.'Authorization' = authHeader
            uri.query = [
                    slug: code,
            ]

            response.success = { resp, result ->
                if (result == null || result.empty) {
                    return null
                }
                def course = result[0]
                return course['id']
            }
            response.failure = failHandler

        }
    }

    protected String getUserId(String email) {
        RESTClient httpClient = new RESTClient(baseUrl)
        httpClient.request(Method.GET, ContentType.JSON) {
            uri.path = "/wp-json/wp/v2/users"
            headers.'Authorization' = authHeader
            uri.query = [
                    search: email,
            ]

            response.success = { resp, result ->
                if (result == null || result.empty) {
                    return null
                }
                def user = result[0]
                return user['id']
            }
            response.failure = failHandler

        }
    }

    protected String createUser(String email, String firstName, String lastName) {
        RESTClient httpClient = new RESTClient(baseUrl)
        httpClient.request(Method.POST, ContentType.JSON) {
            uri.path = "/wp-json/wp/v2/users"
            headers.'Authorization' = authHeader
            body = [
                    username: email,
                    email: email,
                    password: SecurityUtil.generateRandomPassword(8),
                    first_name: firstName,
                    last_name: lastName
            ]

            response.success = { resp, result ->
                if (result == null || result.empty) {
                    logger.error("Empty responce on user create: $email")
                    throw new IllegalStateException()
                }
                return result['id']
            }
            response.failure = failHandler
        }
    }

    protected void enrol(String courseId, String studentId) {
        RESTClient httpClient = new RESTClient(baseUrl)
        httpClient.request(Method.POST, ContentType.JSON) {
            uri.path = "/wp-json/ldlms/v1/sfwd-courses/$courseId/users"
            headers.'Authorization' = authHeader
            body = [
                user_ids:[studentId]
            ]

            response.failure = failHandler
        }
    }

}
