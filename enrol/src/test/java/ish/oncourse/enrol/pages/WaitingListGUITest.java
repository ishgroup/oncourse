package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.ACheckoutTest;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.cookies.ICookiesService;
import org.apache.log4j.Logger;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.internal.test.TestableResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WaitingListGUITest extends ACheckoutTest {
	private static final Logger LOGGER = Logger.getLogger(WaitingListGUITest.class);
	@Before
	public void setup() throws Exception {
		setup("ish/oncourse/enrol/pages/CheckoutGUITest.xml");

	}

	@Test
	public void testSuccessfulPayment() throws InterruptedException {
		getPageTester().getRegistry().getService("testCookiesService", ICookiesService.class).writeCookieValue(CourseClass.SHORTLIST_COOKIE_KEY, "1001");

		//init load checkout
		TestableResponse response = getPageTester().renderPageAndReturnResponse("WaitingListForm/1001");
		assertResponse(response);
		Element element = response.getRenderedDocument().getElementById("addWaitingList");
		assertNotNull(element);
	}

	private void assertResponse(TestableResponse response) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(response.getRenderedDocument());
		assertEquals(200, response.getStatus());
		assertNull("Not Error500 page", response.getRenderedDocument().getElementById("exception"));

	}
}
