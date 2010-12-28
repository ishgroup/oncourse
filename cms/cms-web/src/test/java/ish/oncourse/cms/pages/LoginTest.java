package ish.oncourse.cms.pages;

import ish.oncourse.cms.services.TestModule;

import static org.junit.Assert.assertNotNull;

import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoginTest {
	
	private static final String appPackage = "ish.oncourse.cms";
	private static final String appName = "App";
	
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
		assertNotNull(doc.getDocument().getElementById("email"));
		assertNotNull(doc.getDocument().getElementById("password"));
		tester.shutdown();
	}
	
	@Test
	public void testLoginSuccess() {
		
	}
	
	@Test
	public void testLoginFailure() {
		
	}
}
