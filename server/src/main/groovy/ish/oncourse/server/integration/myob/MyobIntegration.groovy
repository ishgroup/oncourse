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

package ish.oncourse.server.integration.myob

import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.math.Money
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.IntegrationProperty
import ish.oncourse.server.integration.GetProps
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
@Plugin(type = 6)
class MyobIntegration implements PluginTrait {

	public static final String MYOB_API_KEY = "pdkcayputcat4kxfs5s9hkdz"
	public static final String MYOB_CLIENT_SECRET = "8atBVPDBJCYs8Vr4WKxAnBc3"
	public static final String MYOB_ACCESS_CODE_URL = "https://secure.myob.com/oauth2/account/authorize?client_id=" +
			MYOB_API_KEY + "&redirect_uri=http%3A%2F%2Fdesktop&response_type=code&scope=CompanyFile"

	public static final String MYOB_BASE_URL = "baseUrl"
	public static final String MYOB_USER = "user"
	public static final String MYOB_PASSWORD = "password"
	public static final String MYOB_ACCESS_CODE = "accessCode"
	public static final String MYOB_REFRESH_TOKEN = "refreshToken"

	static final REDIRECT_URL = "https://secure-payment.oncourse.net.au/services/myob/auth"
	static final ACCESS_TOKEN_URL = "https://secure.myob.com/oauth2/v1/authorize"

    String baseUrl
    String accessCode
    String refreshToken
    String user
    String password

	private static Logger logger = LogManager.logger

	MyobIntegration(Map args) {
		loadConfig(args)

		this.baseUrl = configuration.getIntegrationProperty(MYOB_BASE_URL).value
		this.refreshToken = configuration.getIntegrationProperty(MYOB_REFRESH_TOKEN).value
		this.user = configuration.getIntegrationProperty(MYOB_USER).value
		this.password = configuration.getIntegrationProperty(MYOB_PASSWORD).value
	}

	def createAccessToken() {
		def client = new RESTClient(ACCESS_TOKEN_URL)
		client.headers['x-myobapi-key'] = MYOB_API_KEY
		client.headers['x-myobapi-version'] = "v2"
		client.request(Method.POST, ContentType.URLENC) {
			body = [
					client_id: MYOB_API_KEY,
					client_secret: MYOB_CLIENT_SECRET,
					scope: "CompanyFile",
					code: accessCode,
					redirect_uri: REDIRECT_URL,
					grant_type: "authorization_code"
			]

			response.success = { resp, result ->
                String accessToken = result['access_token']
                String refreshToken = result['refresh_token']

				// update refresh token value stored in database
				configuration.setProperty(MYOB_REFRESH_TOKEN, refreshToken)
				configuration.context.commitChanges()

				this.refreshToken = refreshToken

				return accessToken
			}
		}
	}

	def refreshAccessToken() {
		def client = new RESTClient(ACCESS_TOKEN_URL)
		client.headers['x-myobapi-key'] = MYOB_API_KEY
		client.headers['x-myobapi-version'] = "v2"
		client.request(Method.POST, ContentType.URLENC) {
			body = [
					client_id: MYOB_API_KEY,
					client_secret: MYOB_CLIENT_SECRET,
					refresh_token: refreshToken,
					grant_type: "refresh_token"
			]

			response.success = { resp, result ->
                String accessToken = result['access_token']
                String refreshToken = result['refresh_token']

				// update refresh token value stored in database
				configuration.setProperty(MYOB_REFRESH_TOKEN, refreshToken)
				configuration.context.commitChanges()

				this.refreshToken = refreshToken

				return accessToken
			}
		}
	}

	def getAccessToken() {
		return refreshToken ? refreshAccessToken() : createAccessToken()
	}

	def getTaxUid(String taxCode) {
		def client = new RESTClient(baseUrl)

		client.headers['Authorization'] = "Bearer ${getAccessToken()}"
		client.headers['x-myobapi-key'] = MYOB_API_KEY
		client.headers['x-myobapi-cftoken'] = Base64.encoder.encodeToString("${user}:${password}".bytes)
		client.headers['x-myobapi-version'] = "v2"
		def result = client.request(Method.GET, ContentType.JSON) {
			uri.path = "GeneralLedger/TaxCode/"
			response.success = { resp, result ->
				return result
			}
		}

		result['Items']?.find { it['Code'] == taxCode }['UID']
	}

	def getAccountUid(String name) {
		def client = new RESTClient(baseUrl)

		client.headers['Authorization'] = "Bearer ${getAccessToken()}"
		client.headers['x-myobapi-key'] = MYOB_API_KEY
		client.headers['x-myobapi-cftoken'] = Base64.encoder.encodeToString("${user}:${password}".bytes)
		client.headers['x-myobapi-version'] = "v2"
		def result = client.request(Method.GET, ContentType.JSON) {
			uri.path = "GeneralLedger/Account/"
			response.success = { resp, result ->
				return result
			}
		}

		result['Items']?.find { it['Name'] == name }['UID']
	}

	def addManualJournal(Map<String, Money> entries) {
		def client = new RESTClient(baseUrl)

		client.headers['Authorization'] = "Bearer ${getAccessToken()}"
		client.headers['x-myobapi-key'] = MYOB_API_KEY
		client.headers['x-myobapi-cftoken'] = Base64.encoder.encodeToString("${user}:${password}".bytes)
		client.headers['x-myobapi-version'] = "v2"
		client.request(Method.POST, ContentType.JSON) {
			uri.path = "GeneralLedger/GeneralJournal"
			body = [
					DateOccured: new Date().format("yyyy-MM-dd'T'HH:mm:ss"),
					Lines: [
							entries.each { entry ->
								[
										Account: [
												UID: getAccountUid(entry.key)
										],
										TaxCode: [
												// FIXME: NO GST is hardcoded for now,
												// need to figure out what tax to set for account transaction records in onCourse
												UID: getTaxUid('FRE')
										],
										Amount: entry.value.abs().toPlainString(),
										IsCredit: !entry.value.negative
								]
							}
					]
			]

			response.success = { resp, result ->
				return result
			}
		}
	}


	protected addManualJournalForTransactions(List<AccountTransaction> transactions) {
		Map<String, Money> transactionsMap = transactions.inject([:] as Map<String, Money>) { result, transaction ->
			result[transaction.account.accountCode] = (result[transaction.account.accountCode] ?: Money.ZERO).add(transaction.account.credit ? transaction.amount : transaction.amount.negate())
			return result
		}
		addManualJournal(transactionsMap)
	}

	@GetProps
	static List<IntegrationProperty> getProps(IntegrationConfiguration configuration) {

		return [configuration.getIntegrationProperty(MYOB_BASE_URL),
				configuration.getIntegrationProperty(MYOB_USER),
				configuration.getIntegrationProperty(MYOB_PASSWORD),
				configuration.getIntegrationProperty(MYOB_REFRESH_TOKEN)]

	}
}
