package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.ACheckoutTest;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.paymentexpress.TestPaymentGatewayService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.internal.test.TestableResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CheckoutGUITest extends ACheckoutTest {
	private static final Logger logger = LogManager.getLogger();
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
		parameters.put("creditCardName", TestPaymentGatewayService.VISA.getName());
		parameters.put("creditCardNumber", TestPaymentGatewayService.VISA.getNumber());
		parameters.put("creditCardCVV", TestPaymentGatewayService.VISA.getCvv());
		parameters.put("expiryMonth", "12");
		parameters.put("expiryYear", "2027");
		parameters.put("userAgreed", "on");

		response = getPageTester().clickSubmitAndReturnResponse(element, parameters);
		assertResponse(response);

		assertTrue("DPS waitng page", response.getRenderedDocument().toString().contains("Please Wait!"));
		//test dps gateway uses 10 sec interval to process payment
		Thread.sleep(15000);
		getPageTester().renderPageAndReturnResponse("Payment");
		response = getPageTester().renderPageAndReturnResponse("Payment");
		assertResponse(response);

		assertTrue(response.getRenderedDocument().toString().contains("SUCCESSFUL"));
	}

	private void assertResponse(TestableResponse response) {
		logger.debug(response.getRenderedDocument());
		assertEquals(200, response.getStatus());
		assertNull("Not Error500 page", response.getRenderedDocument().getElementById("exception"));

	}
}
