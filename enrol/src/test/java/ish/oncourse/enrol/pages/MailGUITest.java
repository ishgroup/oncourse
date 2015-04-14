package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.ACheckoutTest;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.cookies.ICookiesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.internal.test.TestableResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MailGUITest extends ACheckoutTest {
	private static final Logger logger = LogManager.getLogger();
	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/pages/CheckoutGUITest.xml");

	}

	@Test
	public void testSuccessfulPayment() throws InterruptedException {
		getPageTester().getRegistry().getService("testCookiesService", ICookiesService.class).writeCookieValue(CourseClass.SHORTLIST_COOKIE_KEY, "1001");

		//init load checkout
		TestableResponse response = getPageTester().renderPageAndReturnResponse("Mail");
		assertResponse(response);
		Element element = response.getRenderedDocument().getElementById("addstudent-block");
		assertNotNull(element);
	}

	private void assertResponse(TestableResponse response) {
		logger.debug(response.getRenderedDocument());
		assertEquals(200, response.getStatus());
		assertNull("Not Error500 page", response.getRenderedDocument().getElementById("exception"));

	}
}
