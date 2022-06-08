/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.kronos

import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait

@CompileDynamic
@Plugin(type = 19)
class KronosIntegration implements PluginTrait {
    public static final String KRONOS_USERNAME_KEY = "username"
    public static final String KRONOS_PASSWORD_KEY = "password"
    public static final String KRONOS_API_KEY = "apiKey"
    public static final String KRONOS_COMPANY_SHORT_NAME_KEY = "companyShortName"
    public static final String KRONOS_CID_KEY = "CID"

    public static final String KRONOS_REST_URL = "https://secure.workforceready.com.au"
    public static final int HTTP_NOT_FOUND = 404
    /**
     * Error codes from https://secure.workforceready.com.au/ta/docs/rest/pages/errorCodes.html
     */
    public static final int ACCOUNT_NOT_FOUND_CODE = 10002

    String username
    String password
    String apiKey
    String companyShortName
    String CID
    String authToken

    KronosIntegration(Map args) {
        loadConfig(args)

        this.username = configuration.getIntegrationProperty(KRONOS_USERNAME_KEY).value
        this.password= configuration.getIntegrationProperty(KRONOS_PASSWORD_KEY).value
        this.apiKey = configuration.getIntegrationProperty(KRONOS_API_KEY).value
        this.companyShortName = configuration.getIntegrationProperty(KRONOS_COMPANY_SHORT_NAME_KEY).value
        this.CID = configuration.getIntegrationProperty(KRONOS_CID_KEY).value
    }

    /**
     * Auth header will need to be generated every hour or so. So only call this function if you have a very long running script
     * using this KronosIntegration object
     *
     * @return the access token for used as Bearer authentication
     */
    def initAuthHeader() {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Api-Key"] = apiKey
        def result = client.request(Method.POST, ContentType.JSON) {
            uri.path = "/ta/rest/v1/login"
            body = [
                    credentials: [
                        username: username,
                        password: password,
                        company: companyShortName,
                    ]
            ]
            response.success = { resp, result ->
                authToken = result["token"]
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed to get access token  ${resp.getStatusLine()}")
            }
        }

        return result
    }

    /**
     * Return only those employees who the authenticated user can see.
     * The list of returned fields depends on security and company settings.
     * Employees from Kronos via API v1
     *
     * @return employees
     */
    protected getAllEmployeesV1() {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v1/employees/"

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed  ${resp.getStatusLine()}")
            }
        }
    }

    /**
     * Returns basic information about the employees a user has permissions to view,
     * together with links to APIs that provide detailed information about each employee.
     * Employees from Kronos via API v2
     *
     * @return employees
     */
    protected getAllEmployeesV2() {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/employees"

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                throw new IllegalStateException("Failed  ${resp.getStatusLine()}")
            }
        }
    }

    /**
     * Returns the core Employee record containing the bulk of the information about employee.
     * Additionally the response contains links to additional APIs to return additional related data such as Pay Information or Demographics.
     * Employee from Kronos via API v2
     *
     * @return employee
     */
    protected getEmployee(id) {
        def client = new RESTClient(KRONOS_REST_URL)
        client.headers["Authentication"] = "Bearer ${authToken}"
        client.request(Method.GET) {
            uri.path = "/ta/rest/v2/companies/${CID}/employees/${id}"

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                if (resp.getProperties()["status"] == HTTP_NOT_FOUND && result?.errors?.code?.get(0) == ACCOUNT_NOT_FOUND_CODE) {
                    return null
                } else {
                    throw new IllegalStateException("Failed to get employee with id ${id} ${resp.getStatusLine()}")
                }
            }
        }
    }
}
