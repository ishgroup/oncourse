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

package ish.oncourse.server.integration.okta

import groovy.transform.CompileDynamic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.oncourse.server.api.v1.login.SsoIntegrationTrait
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.IntegrationProperty
import ish.oncourse.server.integration.GetProps
import ish.oncourse.server.integration.Plugin
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@CompileDynamic
@Plugin(type = 20)
class OktaIntegration implements SsoIntegrationTrait {
	public static final String OKTA_CLIENT_ID = "oktaClientId"
	public static final String OKTA_CLIENT_SECRET = "oktaClientSecret"
	public static final String OKTA_WEB_REDIRECT = "oktaWebRedirect"
	public static final String OKTA_APP_URL = "oktaAppUrl"

	String clientId
	String clientSecret
	String webRedirect
	String applicationUrl

	private static Logger logger = LogManager.logger

    OktaIntegration(Map args) {
		loadConfig(args)

		this.clientId = configuration.getIntegrationProperty(OKTA_CLIENT_ID).value
		this.clientSecret = configuration.getIntegrationProperty(OKTA_CLIENT_SECRET).value
		this.webRedirect = configuration.getIntegrationProperty(OKTA_WEB_REDIRECT).value
		this.applicationUrl = configuration.getIntegrationProperty(OKTA_APP_URL).value
	}


	String getUserEmailByCode(String activationCode){
		Closure failureHandler = { resp, result ->
			logger.error(resp)
			logger.error(result)
			throw new ClientErrorException('Login failed: '+result, Response.Status.UNAUTHORIZED)
		}

		def userToken = null

		RESTClient client = new RESTClient(applicationUrl)
		client.request(Method.POST, ContentType.JSON) {
			uri.path = "/oauth2/default/v1/token"
			uri.query = [grant_type    : "authorization_code",
						 redirect_uri : webRedirect,
						 code         : activationCode]

			response.success = { resp, result ->
				userToken = result.access_token
			}
			response.failure = failureHandler
		}

		client.headers['Authorization'] = "Bearer ${userToken}"
		client.request(Method.GET, ContentType.JSON) {
			uri.path = "/oauth2/default/v1/userinfo"

			response.success = { resp, result ->
				if(result.email_verified)
					return result.email
				else
					throw new ClientErrorException('Login failed: OKTA email not verified', Response.Status.UNAUTHORIZED)
			}
			response.failure = failureHandler
		}
	}

	@Override
	String getAuthorizationPageLink() {
		return """${applicationUrl}/oauth2/default/v1/authorize?client_id=${clientId}
				&response_type=token
				&prompt=consent
				&scope=email
				&redirect_uri=${webRedirect}
				&state=myState"""
	}

	@GetProps
	static List<IntegrationProperty> getProps(IntegrationConfiguration configuration) {
		return [configuration.getIntegrationProperty(OKTA_CLIENT_ID),
				configuration.getIntegrationProperty(OKTA_CLIENT_SECRET),
				configuration.getIntegrationProperty(OKTA_WEB_REDIRECT),
				configuration.getIntegrationProperty(OKTA_APP_URL)]
	}

}
