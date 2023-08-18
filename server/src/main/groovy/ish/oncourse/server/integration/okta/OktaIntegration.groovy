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
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.IntegrationProperty
import ish.oncourse.server.integration.GetProps
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@CompileDynamic
@Plugin(type = 20)
class OktaIntegration implements PluginTrait {
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


	String authorizeUser(String activationCode){
		Closure failureHandler = { resp, result ->
			logger.error(resp)
			logger.error(result)
			throw new ClientErrorException('Login failed: '+result, Response.Status.UNAUTHORIZED)
		}

		RESTClient client = new RESTClient(applicationUrl)
		client.request(Method.POST, ContentType.JSON) {
			uri.path = "/oauth2/default/v1/token"
			uri.query = [grant_type    : "authorization_code",
						 redirect_uri : webRedirect,
						 code         : activationCode]

			response.success = { resp, result ->
				return result.access_token
			}
			response.failure = failureHandler
		}
	}

	@GetProps
	static List<IntegrationProperty> getProps(IntegrationConfiguration configuration) {
		return [configuration.getIntegrationProperty(OKTA_CLIENT_ID),
				configuration.getIntegrationProperty(OKTA_CLIENT_SECRET),
				configuration.getIntegrationProperty(OKTA_WEB_REDIRECT),
				configuration.getIntegrationProperty(OKTA_APP_URL)]
	}

}
