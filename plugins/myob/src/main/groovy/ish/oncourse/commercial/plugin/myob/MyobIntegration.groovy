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

package ish.oncourse.commercial.plugin.myob

import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.math.Money
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.IntegrationProperty
import ish.oncourse.server.integration.GetProps
import ish.oncourse.server.integration.OnSave
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.server.api.v1.service.impl.IntegrationApiImpl.VERIFICATION_CODE

@CompileDynamic
@Plugin(type = 6)
class MyobIntegration implements PluginTrait {
    public static final String MYOB_BASE_URL = "myobBaseUrl"
    public static final String MYOB_USER = "myobUser"
    public static final String MYOB_PASSWORD = "myobPassword"
    public static final String MYOB_FILE_NAME = "myobFileName"
    public static final String MYOB_REFRESH_TOKEN = "myobRefreshToken"

    public static final String MYOB_API_KEY = "07545d14-9a95-4398-b907-75af3b3841ae"
    public static final String MYOB_CLIENT_SECRET = "fZsdAgh46KV9HX7M0CLC1HRU"
    public static final REDIRECT_URL = "https://secure-payment.oncourse.net.au/services/s/integrations/myob/auth.html"
    static final ACCESS_TOKEN_URL = "https://secure.myob.com/oauth2/v1/authorize/"
    public static final String MYOB_ACCESS_CODE_URL = "https://secure.myob.com/oauth2/account/authorize?client_id=" +
            MYOB_API_KEY + "&redirect_uri=http%3A%2F%2Fdesktop&response_type=code&scope=CompanyFile"

    @OnSave
    static void onSave(IntegrationConfiguration configuration, Map<String, String> props) {
        configuration.setProperty(MYOB_BASE_URL, props[MYOB_BASE_URL])
        configuration.setProperty(MYOB_USER, props[MYOB_USER])
        configuration.setProperty(MYOB_FILE_NAME, props[MYOB_FILE_NAME])

        if (props[MYOB_PASSWORD] != null)
            configuration.setProperty(MYOB_PASSWORD, props[MYOB_PASSWORD])

        def accessCode = props[VERIFICATION_CODE]
        def client = new RESTClient(ACCESS_TOKEN_URL)
        client.request(Method.POST, ContentType.URLENC) {
            body = [
                    client_id    : MYOB_API_KEY,
                    client_secret: MYOB_CLIENT_SECRET,
                    code         : accessCode,
                    redirect_uri : REDIRECT_URL,
                    grant_type   : "authorization_code"
            ]

            response.success = { resp, notParsedResult -> processTokensResponse(resp, notParsedResult, configuration) }

            response.failure = { resp, body ->
                logger.error("Myob app authentication error: " + body.toString())
                throw new RuntimeException("Myob app authentication failed")
            }
        }
    }

    static String processTokensResponse(HttpResponseDecorator resp, HashMap notParsedResult,
                                        IntegrationConfiguration configuration) {
        logger.log(Level.INFO, "tokens response was received")
        String jsonString = (notParsedResult as Map).keySet().find()
        def result = parseJsonAsMap(jsonString)
        String accessToken = result['access_token']
        String refreshToken = result['refresh_token']

        // update refresh token value stored in database
        configuration.setProperty(MYOB_REFRESH_TOKEN, refreshToken)
        configuration.context.commitChanges()
        return accessToken
    }

    static Map<String, String> parseJsonAsMap(String jsonString) {
        def jsonSlurper = new JsonSlurper()
        return jsonSlurper.parseText(jsonString) as Map<String, String>
    }

    String baseUrl
    String refreshToken
    String user
    String password
    String fileGuid

    private static Logger logger = LogManager.logger


    MyobIntegration(Map args) {
        loadConfig(args)

        this.baseUrl = configuration.getIntegrationProperty(MYOB_BASE_URL).value
        this.refreshToken = configuration.getIntegrationProperty(MYOB_REFRESH_TOKEN).value
        this.user = configuration.getIntegrationProperty(MYOB_USER).value

        def passwordProp = configuration.getIntegrationProperty(MYOB_PASSWORD)
        this.password = password != null ? passwordProp.value : ""

        def fileName = configuration.getIntegrationProperty(MYOB_FILE_NAME).value
        this.fileGuid = getFileGuidByName(fileName)
    }

    private String getFileGuidByName(String fileName) {
        def client = buildDefaultAuthorizedClientFor(baseUrl)
        return client.request(Method.GET, ContentType.JSON) {
            response.success = { resp, notParsedResult ->
                def map = (notParsedResult as ArrayList<Map<String, String>>)?.find { it['Name'] == fileName }
                if (map == null) {
                    sendErrorMessage("Myob configuration error",
                            "Myob configuration failed: file with name $fileName was not found in myob.")
                    failWithCheckedError()
                }

                return map['Id']
            }
            response.failure = { resp, body ->
                logger.error("Get file by name error: " + body.toString())
                failWithInnerError()
            }
        }
    }


    public void addManualJournalForTransactions(List<AccountTransaction> transactions) {
        Map<String, Money> transactionsMap = transactions.inject([:] as Map<String, Money>) { result, transaction ->
            result[transaction.account.accountCode] = (result[transaction.account.accountCode] ?: Money.ZERO).add(transaction.account.credit ? transaction.amount : transaction.amount.negate())
            return result
        }
        addManualJournal(transactionsMap)
    }

    def addManualJournal(Map<String, Money> entries) {
        String url = fileGuid == null ? baseUrl : baseUrl + "/" + fileGuid + "/"
        def client = buildDefaultAuthorizedClientFor(url)
        client.request(Method.POST, ContentType.JSON) {
            uri.path = "GeneralLedger/GeneralJournal"
            body = [
                    DateOccurred: new Date().format("yyyy-MM-dd'T'HH:mm:ss"),
                    Lines       : entries.collect { entry ->
                        [
                                Account : [
                                        UID: getAccountUid(entry.key)
                                ],
                                Amount  : entry.value.abs().toPlainString(),
                                IsCredit: !entry.value.negative
                        ]
                    }
            ]

            response.success = { resp, result ->
                return result
            }

            response.failure = { resp, body ->
                logger.error("Add manual journal error: " + body.toString())
                sendErrorMessage("Myob transactions export error",
                        "Transactions export failed: $body.")
                failWithCheckedError()
            }
        }
    }

    private getAccountUid(String displayId) {
        String url = fileGuid == null ? baseUrl : baseUrl + "/" + fileGuid + "/GeneralLedger/Account"
        def result = getMapByUrl(url)
        def resMap = result['Items']?.find { it['DisplayID'] == displayId }
        if (resMap != null)
            return resMap['UID']
        else {
            logger.error("Account not registered in myob: $displayId")
            sendErrorMessage("Myob transactions export error",
                    "Transactions export failed: account with code $displayId was not found in myob file;\n all transactions were not saved.")
            failWithCheckedError()
        }
    }

    private Map<String, String> getMapByUrl(String url) {
        def client = buildDefaultAuthorizedClientFor(url)
        return client.request(Method.GET, ContentType.JSON) {
            response.success = { resp, result ->
                return result
            }
            response.failure = { resp, body ->
                logger.error("Get map by url error: " + body.toString())
                failWithInnerError()
            }
        } as Map<String, String>
    }

    private RESTClient buildDefaultAuthorizedClientFor(String url) {
        def client = new RESTClient(url)

        client.headers['Authorization'] = "Bearer ${getUpdatedToken()}"
        client.headers['x-myobapi-key'] = MYOB_API_KEY
        client.headers['x-myobapi-cftoken'] = Base64.encoder.encodeToString("${user}:${password}".bytes)
        client.headers['x-myobapi-version'] = "v2"
        return client
    }

    private getUpdatedToken() {
        def client = new RESTClient(ACCESS_TOKEN_URL)
        client.request(Method.POST, ContentType.URLENC) {
            body = [
                    client_id    : MYOB_API_KEY,
                    client_secret: MYOB_CLIENT_SECRET,
                    refresh_token: configuration.getProperty(MYOB_REFRESH_TOKEN).value,
                    grant_type   : "refresh_token"
            ]

            response.success = { resp, notParsedResult -> processTokensResponse(resp, notParsedResult, configuration) }

            response.failure = { resp, body ->
                logger.error("Update access token error: " + body.toString())
                failWithInnerError()
            }
        }
    }

    private sendErrorMessage(String title, String reason) {
        emailService.email {
            subject("onCourse->$title")
            content("$reason")
            from(preferenceController.emailFromAddress)
            to(preferenceController.emailAdminAddress)
        }
    }

    private failWithInnerError(){
        throw new RuntimeException("Export failed with inner error")
    }

    private failWithCheckedError(){
        throw new RuntimeException("Transactions export failed. Check email for details")
    }

    @GetProps
    static List<IntegrationProperty> getProps(IntegrationConfiguration configuration) {

        return [configuration.getIntegrationProperty(MYOB_BASE_URL),
                configuration.getIntegrationProperty(MYOB_USER),
                configuration.getIntegrationProperty(MYOB_PASSWORD),
                configuration.getIntegrationProperty(MYOB_FILE_NAME),
                configuration.getIntegrationProperty(MYOB_REFRESH_TOKEN)]

    }
}
