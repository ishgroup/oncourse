package ish.oncourse.cms.pages;

import ish.oncourse.cms.services.TestModule;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class LoginTest {

	private static final String appPackage = "ish.oncourse.cms";
	private static final String appName = "cms_test";

	@Before
	public void setup() {

	}

	@After
	public void tearDown() {

	}

	@Test
	public void testPageLoaded() {
		PageTester tester = new PageTester(appPackage, appName, "src/main/webapp", TestModule.class);
		Document doc = tester.renderPage("Login");
		assertNotNull(doc.getElementById("logEmail"));
		assertNotNull(doc.getElementById("password"));
		tester.shutdown();
	}

	@Test
	public void testLoginSuccess() {
		PageTester tester = new PageTester(appPackage, appName, "src/main/webapp", TestModule.class);

		Document doc = tester.renderPage("Login");

		Element loginForm = doc.getElementById("loginForm");

		Map<String, String> fieldValues = new HashMap<>();

		fieldValues.put("logEmail", "test@right.com");
		fieldValues.put("password", "rpasswd");

		try {
			doc = tester.submitForm(loginForm, fieldValues);
			fail("no document here expected, we should get redirect.");
		}
		catch (RuntimeException e) {
			
		}
		
		tester.shutdown();
	}

	@Test
	public void testLoginFailure() {
		PageTester tester = new PageTester(appPackage, appName, "src/main/webapp", TestModule.class);

		Document doc = tester.renderPage("Login");

		Element loginForm = doc.getElementById("loginForm");

		Map<String, String> fieldValues = new HashMap<>();

		fieldValues.put("email", "test@right.com");
		fieldValues.put("password", "wpasswd");

		doc = tester.submitForm(loginForm, fieldValues);
		
		//expected to see the same form but with errors
		Element form = doc.getElementById("loginForm");
		assertNotNull(form);
		
		Element errorsDiv = form.getElementByAttributeValue("class", "t-error");
		assertNotNull(errorsDiv);
		
		assertTrue(errorsDiv.getChildren().size() > 0);

		tester.shutdown();
	}
}
