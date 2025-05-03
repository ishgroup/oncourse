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

package ish.oncourse.server.integration.talentlms

import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.common.types.IntegrationType
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.IntegrationProperty
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.util.SecurityUtil

@CompileDynamic
@Plugin(type = IntegrationType.TALENT_LMS)
class TalentLMSIntegration implements PluginTrait {
    public static final String TALENT_LMS_API_KEY = "apiKey"
    public static final String TALENT_LMS_URL = "baseUrl"

    IntegrationProperty apiKey
    IntegrationProperty baseUrl
    IntegrationConfiguration integration

    TalentLMSIntegration(Map args) {
        loadConfig(args)

        apiKey = integration.getIntegrationProperty("apiKey")
        baseUrl = integration.getIntegrationProperty("baseUrl")
    }

    protected String getCourseId(String code) {
        RESTClient httpClient = new RESTClient(baseUrl.value)
        httpClient.request(Method.GET, ContentType.JSON) {
            uri.path = "/api/v1/courses"
            headers.'Authorization' = authHeader


            response.success = { resp, result ->
                if (result == null || result.empty) {
                    return null
                }
                def course = result.find { it['code'] == code }
                if (course) {
                    return course['id']
                } else {
                    return null
                }
            }
        }

    }

    protected String getUserId(String email) {

        RESTClient httpClient = new RESTClient(baseUrl.value)
        httpClient.request(Method.GET, ContentType.JSON) {
            uri.path = "/api/v1/users/email:$email"
            headers.'Authorization' = authHeader


            response.success = { resp, result ->
               if (!result || result.empty) {
                   return null
               }
                return result['id']
            }
            response.failure = { HttpResponseDecorator resp  ->
                return null
            }
        }

    }

    protected String createUser(String email, String firstName, String lastName) {
        RESTClient httpClient = new RESTClient(baseUrl.value)
        httpClient.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/usersignup"
            headers.'Authorization' = authHeader
            body = [
                    login: email,
                    first_name: firstName,
                    last_name: lastName,
                    email: email,
                    user_type: "Learner-Type",
                    password: SecurityUtil.generateRandomPassword(8)
            ]

            response.success = { resp, result ->
                if (!result || result.empty) {
                   throw new IllegalArgumentException("Empty responce for user creation: $email")
                }
                return result['id']
            }
        }
    }

    protected void enrol(String courseId, String userId) {
        RESTClient httpClient = new RESTClient(baseUrl.value)
        httpClient.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/addusertocourse"
            headers.'Authorization' = authHeader
            body = [
                    user_id: userId,
                    course_id: courseId,
                    role: 'learner'
            ]
        }
    }

    private String getAuthHeader() {
        return  "Basic ${"${apiKey.value}:".bytes.encodeBase64().toString()}"
    }
}
