/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.xero

import groovy.json.JsonSlurper
import groovyx.net.http.RESTClient
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST

class AuthentificationContext {
    // ish group developer credentials
    static final String XERO_CLIENT_ID = "A05FD21034974F29ABD4301FC54513BC"
    static final String XERO_CLIENT_SECRET ="tGJcGIqd0U2Ok4EJBO-7KNbBtkgcbKGD9k5osIm7Jl745V0o"
    private static Logger logger = LogManager.logger

    private String oldRefreshToken
    
    String newAccessToken
    String newRefreshToken
    String tenantId

    AuthentificationContext(String oldRefreshToken) {
        this.oldRefreshToken = oldRefreshToken
    }
    
    void init() {
        new RESTClient('https://identity.xero.com').request(POST, URLENC) {
            uri.path = '/connect/token'
            headers.'Authorization' = "Basic ${"$XERO_CLIENT_ID:$XERO_CLIENT_SECRET".bytes.encodeBase64().toString()}"
            body = "grant_type=refresh_token&refresh_token=${oldRefreshToken}"

            response.success = { resp, body ->
                Map<String, String> jsonResponce = new JsonSlurper().parseText(body.keySet()[0])
                newAccessToken = jsonResponce['access_token']
                newRefreshToken = jsonResponce['refresh_token']
            }

            response.failure = { resp, body ->
                String error = body.keySet()[0]
                logger.error(error)

                throw new RuntimeException("Xero app refresh token error error: $error")
            }
        }

        new RESTClient(XeroIntegration.XERO_API_BASE).request(GET, JSON) {
            uri.path = '/connections'
            headers.'Authorization' = "Bearer $newAccessToken"

            response.success = { resp, body ->
                this.tenantId = body[0]['tenantId']
            }
        }
    }

}
