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
	public void testSuccessfulPayment() throws InterruptedException {
		getPageTester().getRegistry().getService("testCookiesService", ICookiesService.class).writeCookieValue(CourseClass.SHORTLIST_COOKIE_KEY, "1001");

		//init load checkout
		TestableResponse response = getPageTester().renderPageAndReturnResponse("Checkout");
		assertResponse(response);
		Element element = response.getRenderedDocument().getElementById("submitContact");
		assertNotNull(element);

		//add the first student
		Map<String,String> parameters = new HashMap<>();
		parameters.put("firstName", "Student1");
		parameters.put("lastName", "Student1");
		parameters.put("email", "Student1@Student1.net");
		response = getPageTester().clickSubmitAndReturnResponse(element, parameters);
		assertResponse(response);

		element = response.getRenderedDocument().getElementById("proceedToPaymentEvent");
		assertNotNull(element);

		//press proceedToPayment
		response = getPageTester().clickLinkAndReturnResponse(element);
		assertResponse(response);

		element = response.getRenderedDocument().getElementById("paymentSubmit");
		assertNotNull(element);

		//fill credit card details and press  paymentSubmit
		parameters = new HashMap<>();
		parameters.put("contact", "1001");
		parameters.put("creditCardName", "Student1 Student1");
		parameters.put("creditCardNumber", "5105105105105100");
		parameters.put("creditCardCVV", "1111");
		parameters.put("expiryMonth", "01");
		parameters.put("expiryYear", "2111");
		parameters.put("userAgreed", "on");

		response = getPageTester().clickSubmitAndReturnResponse(element, parameters);
		assertResponse(response);

		assertTrue("DPS wating page", response.getRenderedDocument().toString().contains("Please Wait!"));
		//test dps gateway uses 10 sec interval to process payment
		Thread.sleep(15000);
		getPageTester().renderPageAndReturnResponse("Payment");
		response = getPageTester().renderPageAndReturnResponse("Payment");
		assertResponse(response);

		assertTrue(response.getRenderedDocument().toString().contains("SUCCESSFUL"));
	}

	private void assertResponse(TestableResponse response) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(response.getRenderedDocument());
		assertEquals(200, response.getStatus());
		assertNull("Not Error500 page", response.getRenderedDocument().getElementById("exception"));

	}
}
