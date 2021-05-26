/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.xero

import groovy.json.JsonSlurper
import groovyx.net.http.RESTClient
import ish.oncourse.server.cayenne.IntegrationConfiguration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.DELETE
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST

class AuthentificationContext {
    // ish group developer credentials
    static final String XERO_CLIENT_ID = "A05FD21034974F29ABD4301FC54513BC"
    static final String XERO_CLIENT_SECRET ="tGJcGIqd0U2Ok4EJBO-7KNbBtkgcbKGD9k5osIm7Jl745V0o"
    private static Logger logger = LogManager.logger

    static final String XERO_ACCESS_TOKEN = "accessToken"
    static final String XERO_REFRESH_TOKEN = "refreshToken"
    
    IntegrationConfiguration integrationConfiguration    
    String newAccessToken
    String newRefreshToken
    String tenantId
    String connectionId

    AuthentificationContext(IntegrationConfiguration integrationConfiguration) {
        this.integrationConfiguration = integrationConfiguration
    }
    
    void init() {
        new RESTClient('https://identity.xero.com').request(POST, URLENC) {
            uri.path = '/connect/token'
            headers.'Authorization' = "Basic ${"$XERO_CLIENT_ID:$XERO_CLIENT_SECRET".bytes.encodeBase64().toString()}"
            body = "grant_type=refresh_token&refresh_token=${integrationConfiguration.getIntegrationProperty(XERO_REFRESH_TOKEN).value}"

            response.success = { resp, body ->
                Map<String, String> jsonResponce = new JsonSlurper().parseText(body.keySet()[0])
                newAccessToken = jsonResponce['access_token']
                newRefreshToken = jsonResponce['refresh_token']
                integrationConfiguration.setProperty(XERO_ACCESS_TOKEN,newAccessToken)
                integrationConfiguration.setProperty(XERO_REFRESH_TOKEN, newRefreshToken)
            }

            response.failure = { resp, body ->
                logger.error(body.toString())
                throw new RuntimeException("Xero app refresh token error error: ${body.toString()}")
            }
        }

        new RESTClient(XeroIntegration.XERO_API_BASE).request(GET, JSON) {
            uri.path = '/connections'
            headers.'Authorization' = "Bearer $newAccessToken"

            response.success = { resp, body ->
                this.tenantId = body[0]['tenantId']
                this.connectionId = body[0]['id']
            }
        }
    }
    
    void disconnect() {
        new RESTClient(XeroIntegration.XERO_API_BASE).request(DELETE, JSON) {
            uri.path = "/connections/$connectionId"
            headers.'Authorization' = "Bearer $newAccessToken"

            response.success = { resp, body ->
                logger.warn("Xero integrstion disconnected")
            }
            
            response.failure = { resp, body ->
                logger.error(body.toString())
                throw new RuntimeException("Xero integrstion disconnect error: ${body.toString()}")
            }
        }
    }
    
    

}
