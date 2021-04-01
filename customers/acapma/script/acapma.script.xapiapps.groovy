/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseException
import groovyx.net.http.ContentType

String URL = ""
String publicToken = ""
String secretToken = ""
String groupStoreKey = ""

Xapiapps xapiapps = new Xapiapps(URL, publicToken, secretToken, groupStoreKey)
def users = xapiapps.searchUser(record.email)
if ((users['results'] as List).size() == 0) {
    try {
        def result = xapiapps.createUser(record.email, record.fullName)
        if (result != 200) {
            throw new IllegalStateException("Something went wrong\n"+result)
        }
    } catch (HttpResponseException ex) {
        String error = "${ex.statusCode} - ${ex.reasonPhrase}"
        throw new IllegalStateException(error)
    }
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

    def searchUser(String email) {
        def httpClient = new RESTClient(url + "/api2/r/search")
        httpClient.request(Method.POST, ContentType.JSON) {
            body = [
                    ipubtoken: publicToken,
                    itoken: secretToken,
                    indexname: "Person2",
                    query: "DOC_EMAIL: " + email
            ]

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                logger.error(resp)
            }
        }
    }

    def createUser(String email, String name) {
        String key = UUID.randomUUID().toString()
        def httpClient = new RESTClient(url + "/api/w/event")
        def args = [
                'contentType': "*/*",
                'requestContentType': "application/json",
                'body': [
                        ipubtoken: publicToken,
                        itoken: secretToken,
                        type: "Person2_create",
                        key: key,
                        name: name,
                        email: email,
                        RTO: true
                ]
        ]
        httpClient.post(args, { result ->
            return result.status
        })
    }

    def history() {
        long startOfDay = LocalDateUtils.valueToDate(LocalDate.now(LocalDateUtils.UTC)).getTime() * 1000
        long endOfDay = LocalDateUtils.valueToDate(LocalDate.now(LocalDateUtils.UTC), true).getTime() * 1000
        def httpClient = new RESTClient(url + "/api2/r/search")
        httpClient.request(Method.POST, ContentType.JSON) {
            body = [
                    ipubtoken: publicToken,
                    itoken: secretToken,
                    indexname: "Task",
                    query: "DOC_OPSTATUS:complete AND learnergroups:${groupStoreKey} AND (DOC_TIMING_COMPLETEAT_TS >= 1613050200000 AND DOC_TIMING_COMPLETEAT_TS < 1613136600000)"
            ]

            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, result ->
                logger.error(resp)
            }
        }
    }
}