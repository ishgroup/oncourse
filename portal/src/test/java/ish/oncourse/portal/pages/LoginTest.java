package ish.oncourse.portal.pages;

import ish.oncourse.portal.service.TestModule;
import ish.oncourse.webservices.usi.TestUSIServiceEndpoint;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class LoginTest {

	private static final String appPackage = "ish.oncourse.portal";
	private static final String appName = "App";
	
	@Test
	@Ignore
	public void testPageLoaded() {
		PageTester tester = new PageTester(appPackage, appName, "src/main/webapp", TestModule.class);
		
		assertNotNull(tester);
		Document doc = tester.renderPage("Login");
		assertNotNull(doc.getElementById("email"));
		assertNotNull(doc.getElementById("password"));
		tester.shutdown();
	}
	
	@Before
	public void setup() {
		System.setProperty(TestUSIServiceEndpoint.USI_TEST_MODE, "true");
	}

	@After
	public void tearDown() {

	}

}
