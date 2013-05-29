package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.ACheckoutTest;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.cookies.ICookiesService;
import org.apache.log4j.Logger;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.internal.test.TestableResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CheckoutGUITest extends ACheckoutTest {
	private static final Logger LOGGER = Logger.getLogger(CheckoutGUITest.class);
	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/pages/CheckoutGUITest.xml");

	}

	@Test
	public void testCheckoutCompiling()
	{
		getPageTester().getRegistry().getService("testCookiesService", ICookiesService.class).writeCookieValue(CourseClass.SHORTLIST_COOKIE_KEY, "1001");
		TestableResponse response = getPageTester().renderPageAndReturnResponse("Checkout");
		assertResponse(response);
		Element element = response.getRenderedDocument().getElementById("submitContact");
		assertNotNull(element);


		Map<String,String> parameters = new HashMap<>();
		parameters.put("firstName", "Student1");
		parameters.put("lastName", "Student1");
		parameters.put("email", "Student1@Student1.net");
		response = getPageTester().clickSubmitAndReturnResponse(element, parameters);
		assertResponse(response);

		element = response.getRenderedDocument().getElementById("proceedToPaymentEvent");
		assertNotNull(element);

		response = getPageTester().clickLinkAndReturnResponse(element);
		assertResponse(response);
	}

	private void assertResponse(TestableResponse response) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(response.getOutput());
		assertEquals(200, response.getStatus());
		assertNull("Not Error500 page", response.getRenderedDocument().getElementById("exception"));

	}
}
