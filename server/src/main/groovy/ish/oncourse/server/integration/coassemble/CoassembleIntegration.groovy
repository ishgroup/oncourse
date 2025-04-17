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

package ish.oncourse.server.integration.coassemble

import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.common.types.IntegrationType
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
@Plugin(type = IntegrationType.COASSEMBLE)
class CoassembleIntegration implements PluginTrait {
    public static final String COASSEMBLE_BASE_URL_KEY = "baseUrl"
    public static final String COASSEMBLE_API_KEY = "apiKey"
    public static final String COASSEMBLE_USER_ID_KEY = "userId"

    String baseUrl
    String apiKey
    String userId

    private static Logger logger = LogManager.logger

    CoassembleIntegration(Map args) {
        loadConfig(args)

        this.baseUrl = configuration.getIntegrationProperty(COASSEMBLE_BASE_URL_KEY).value
        this.apiKey = configuration.getIntegrationProperty(COASSEMBLE_API_KEY).value
        this.userId = configuration.getIntegrationProperty(COASSEMBLE_USER_ID_KEY).value
    }

    protected getCourses() {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "ECOACH-V1-SHA256 UserId=${userId}, UserToken=${apiKey}"
        client.headers["Content-Type"] = "application/json"
        client.headers["Accept"] = "application/json"

        client.request(Method.GET, ContentType.JSON) {
            uri.path = "/api/v1/courses"
            response.success = { resp, result ->
                return result
            }
        }
    }

    protected getGroups(courseId) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "ECOACH-V1-SHA256 UserId=${userId}, UserToken=${apiKey}"
        client.headers["Content-Type"] = "application/json"
        client.headers["Accept"] = "application/json"


        client.request(Method.GET, ContentType.JSON) {
            uri.path = "/api/v1/courses/${courseId}/groups"

            response.success = { resp, result ->
                return result
            }
        }
    }

    protected postMember(enrolment) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "ECOACH-V1-SHA256 UserId=${userId}, UserToken=${apiKey}"
        client.headers["Content-Type"] = "application/json"
        client.headers["Accept"] = "application/json"

        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/members"
            uri.query = [
                    username : enrolment.student.contact.email,
                    firstname: enrolment.student.contact.firstName,
                    lastname : enrolment.student.contact.lastName,
                    email    : enrolment.student.contact.email
            ]

            response.success = { resp, result ->
                return result
            }
        }
    }

    protected postGroup(enrolment, courseId) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "ECOACH-V1-SHA256 UserId=${userId}, UserToken=${apiKey}"
        client.headers["Content-Type"] = "application/json"
        client.headers["Accept"] = "application/json"

        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/courses/${courseId}/groups"
            body = [
                    name: enrolment.courseClass.uniqueCode
            ]

            response.success = { resp, result ->
                return result
            }
        }
    }

    protected enrolMemberToGroup(memberId, groupId) {
        def client = new RESTClient(baseUrl)
        client.headers["Authorization"] = "ECOACH-V1-SHA256 UserId=${userId}, UserToken=${apiKey}"
        client.headers["Content-Type"] = "application/json"
        client.headers["Accept"] = "application/json"

        def id = [memberId] as int[]

        client.request(Method.POST, ContentType.JSON) {
            uri.path = "/api/v1/groups/${groupId}/students"
            body = [
                    ids: id
            ]

            response.success = { resp, result ->
                return result
            }
        }
    }
}
