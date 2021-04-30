/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.services

import ish.CayenneIshTestCase
import ish.oncourse.server.AngelServerFactory
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.servlet.HealthCheckServlet
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.jetty.testing.AngelServletTester
import org.eclipse.jetty.testing.HttpTester
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals

/**
 */
class ServletsTest extends CayenneIshTestCase {

	private static final Logger logger = LogManager.getLogger()

	private static AngelServletTester tester

	@BeforeClass
	static void setupOnce() throws Exception {
		// simulate the servlet setup code as it is in the HttpServer
		tester = new AngelServletTester(
				null,
				injector.getInstance(PreferenceController.class),
				injector.getInstance(AngelServerFactory.class))
		tester.setContextPath("/")
		tester.addServlet(HealthCheckServlet.class, HealthCheckServlet.PATH)
		tester.start()
	}

	@AfterClass
	static void tearDown() throws Exception {
		tester.stop()
	}

	@Test
	void testHealthCheck() throws Exception {
		HttpTester.Request request = HttpTester.newRequest()

		request.setMethod("GET")
		request.setURI(HealthCheckServlet.PATH)
		request.setVersion("HTTP/1.0")

		HttpTester.Response response = HttpTester.parseResponse(tester.getResponses(request.generate()))

		assertEquals("Checking HTTP error code.", 200, response.getStatus())
	}

	@Test
	void testHealthCheckPost() throws Exception {
		HttpTester.Request request = HttpTester.newRequest()

		request.setMethod("POST")
		request.setURI(HealthCheckServlet.PATH)
		request.setVersion("HTTP/1.0")

		HttpTester.Response response = HttpTester.parseResponse(tester.getResponses(request.generate()))

		assertEquals(400, response.getStatus())
		assertEquals("Bad Request", response.getReason())	}

	@Test
	void testHealthCheckPut() throws Exception {
		// test default fallback page:
		HttpTester.Request request = HttpTester.newRequest()

		request.setMethod("PUT")
		request.setURI(HealthCheckServlet.PATH)
		request.setVersion("HTTP/1.0")

		HttpTester.Response response = HttpTester.parseResponse(tester.getResponses(request.generate()))

		assertEquals(400, response.getStatus())
		assertEquals("Bad Request", response.getReason())
	}
}
