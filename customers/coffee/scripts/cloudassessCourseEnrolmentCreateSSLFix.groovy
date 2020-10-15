/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import ish.integrations.IntegrationsConfiguration
import ish.oncourse.server.cayenne.Integration
import ish.oncourse.server.integration.cloudassess.CloudAssessIntegration
import org.apache.cayenne.query.ObjectSelect
import org.apache.http.client.HttpClient
import org.apache.http.client.config.CookieSpecs
import org.apache.http.config.Lookup
import org.apache.http.config.RegistryBuilder
import org.apache.http.cookie.CookieSpecProvider
import org.apache.http.cookie.params.CookieSpecPNames
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.cookie.DefaultCookieSpecProvider
import org.apache.http.impl.cookie.IgnoreSpecProvider
import org.apache.http.impl.cookie.NetscapeDraftSpecProvider
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider
import org.apache.http.params.HttpParams

import static ish.oncourse.server.integration.cloudassess.CloudAssessIntegration.BASE_URL
import static ish.oncourse.server.integration.cloudassess.CloudAssessIntegration.COURSE_ENROLMENT_URL
import static ish.oncourse.server.integration.cloudassess.CloudAssessIntegration.COURSE_URL
import static ish.oncourse.server.integration.cloudassess.CloudAssessIntegration.LOGIN_URL
import static ish.oncourse.server.integration.cloudassess.CloudAssessIntegration.MEMBERSHIP_URL


def run(args) {
	def e = args.entity

	if ( e.courseClass.deliveryMode == ish.common.types.DeliveryMode.ONLINE || e.courseClass.course.code == "baristacourse" ) {

		Integration integration = ObjectSelect.query(Integration).where(Integration.NAME.eq('cloud assess')).selectOne(args.context)

		if (integration == null) {
			throw new IllegalArgumentException('No integration with name \"cloud assess\" was found.')
		}

		CloudAssessIntegration cloudAssess = new CloudAssessIntegration(
				username: integration.getProperty(IntegrationsConfiguration.CLOUDASSESS_USERNAME).value,
				apiKey: integration.getProperty(IntegrationsConfiguration.CLOUDASSESS_API_KEY).value,
				orgId: integration.getProperty(IntegrationsConfiguration.CLOUDASSESS_ORG_ID).value)


		cloudAssess.metaClass.login = {
			def httpClient = new IshRESTClient(BASE_URL)

			httpClient.request(Method.POST, ContentType.JSON) {
				uri.path = LOGIN_URL
				body = [
						username: username,
						api_key1: apiKey,
						client_id: clientId,
						client_secret: clientSecret
				]

				response.success = { resp, result ->
					result
				}

				response.failure = { resp, result ->
					throw new IllegalStateException("Login to Cloud Assess failed: ${result.response}")
				}
			}
		}

		cloudAssess.metaClass.courseSearch = { accessToken, courseCode ->

			def httpClient = new IshRESTClient(BASE_URL)

			httpClient.request(Method.GET, ContentType.JSON) {
				uri.path = COURSE_URL

				uri.query = [
						access_token: accessToken,
						organisation_id: orgId,
						search: courseCode
				]

				response.success = { resp, result ->
					result
				}

				response.failure = { resp, result ->
					throw new IllegalStateException("Course search failed: ${result.response}")
				}
			}
		}

		cloudAssess.metaClass.membershipSearch = { accessToken, email ->
			def httpClient = new IshRESTClient(BASE_URL)

			httpClient.request(Method.GET, ContentType.JSON) {
				uri.path = MEMBERSHIP_URL

				uri.query = [
						access_token: accessToken,
						organisation_id: orgId,
						search_role_id: 30,
						search: email
				]

				response.success = { resp, result ->
					result
				}

				response.failure = { resp, result ->
					throw new IllegalStateException("Membership search failed: ${result.response}")
				}
			}
		}

		cloudAssess.metaClass.membershipCreate = { accessToken, email, firstName, lastName, studentNumber, angelId ->
			def httpClient = new IshRESTClient(BASE_URL)

			httpClient.request(Method.POST, ContentType.JSON) {
				uri.path = MEMBERSHIP_URL
				body = [
						access_token: accessToken,
						membership: [
								organisation_id: orgId,
								email: email,
								first_name: firstName,
								last_name: lastName,
								role_id: 30,
								active: true,
								student_number: studentNumber,
								external_user_id: angelId
						]
				]

				response.success = { resp, result ->
					result
				}

				response.failure = { resp, result ->
					throw new IllegalStateException("Membership creation failed: ${result.response}")
				}
			}
		}
		cloudAssess.metaClass.enrolmentCreate = { accessToken, courseId, membershipId ->
			def httpClient = new IshRESTClient(BASE_URL)

			httpClient.request(Method.POST, ContentType.JSON) {
				uri.path = COURSE_ENROLMENT_URL
				body = [
						access_token    : accessToken,
						course_enrolment: [
								course_id         : courseId,
								membership_id     : membershipId,
								enrol_in_all_units: true
						]
				]

				response.success = { resp, result ->
					result
				}

				response.failure = { resp, result ->
					throw new IllegalStateException("Course enrolment creation failed: ${result.response}")
				}
			}
		}

		cloudAssess.enrol(e, null)

		e.addTag("LMS/CloudAssess")
		args.context.commitChanges()

	}
}


class IshRESTClient extends RESTClient {


	IshRESTClient(Object defaultURI) throws URISyntaxException {
		super(defaultURI)
	}

	protected HttpClient createClient(HttpParams params ) {

		CookieSpecProvider defaultProvider = new DefaultCookieSpecProvider(DefaultCookieSpecProvider.CompatibilityLevel.DEFAULT, null, (String[])params.getParameter(CookieSpecPNames.DATE_PATTERNS), false)
        CookieSpecProvider laxStandardProvider = new RFC6265CookieSpecProvider(
				RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED, null)
        CookieSpecProvider strictStandardProvider = new RFC6265CookieSpecProvider(
				RFC6265CookieSpecProvider.CompatibilityLevel.STRICT, null)
        RegistryBuilder<CookieSpecProvider> registryBuilder = RegistryBuilder.<CookieSpecProvider>create()
				.register(CookieSpecs.DEFAULT, defaultProvider)
				.register('best-match', defaultProvider)
				.register('compatibility', defaultProvider)
				.register(CookieSpecs.STANDARD, laxStandardProvider)
				.register(CookieSpecs.STANDARD_STRICT, strictStandardProvider)
				.register(CookieSpecs.NETSCAPE, new NetscapeDraftSpecProvider())
				.register(CookieSpecs.IGNORE_COOKIES, new IgnoreSpecProvider())

        Lookup<CookieSpecProvider> specProviderLookup = registryBuilder.build()
		HttpClientBuilder clientBuilder = HttpClientBuilder.create()
		clientBuilder.setDefaultCookieSpecRegistry(specProviderLookup)

		return clientBuilder.build()
	}
}
