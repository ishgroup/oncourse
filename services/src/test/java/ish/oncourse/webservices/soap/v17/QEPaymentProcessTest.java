package ish.oncourse.webservices.soap.v17;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.payment.*;
import ish.oncourse.webservices.util.GenericParameterEntry;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Node;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import static org.junit.Assert.*;

/**
 * Use cases described in squish task
 * https://squish.ish.com.au/tasks/17142
 */
public abstract class QEPaymentProcessTest extends RealWSTransportTest {


	protected final void testRenderPaymentPageWithReverseInvoice(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = testEnv.getTestEnv().getPageTester().renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);

		Element paymentForm = doc.getElementById("paymentDetailsForm");
		assertNotNull("Payment form should be visible ", paymentForm);
		Element cardName = paymentForm.getElementById("cardName");
		assertNotNull("Card name input should be available", cardName);
		Element cardNumber = paymentForm.getElementById("cardNumber");
		assertNotNull("Card number input should be available", cardNumber);
		Element expirityMonth = paymentForm.getElementById("expiryMonth");
		assertNotNull("Card expirity month input should be available", expirityMonth);
		Element expirityYear = paymentForm.getElementById("expiryYear");
		assertNotNull("Card expirity year input should be available", expirityYear);
		Element cardCVV = paymentForm.getElementById("cardCvv");
		assertNotNull("Card CVV input should be available", cardCVV);
		Element submitButton = paymentForm.getElementById("pay-button");
		assertNotNull("Payment form submit should be available", submitButton);

		//get session to check that processing in progress
		Request request = testEnv.getTestEnv().getPageTester().getService(TestableRequest.class);
		Session session = request.getSession(false);
		assertNull("Session should be null", session);

		NewPaymentProcessController controller = testEnv.getTestEnv().getPaymentProcessController(sessionId);

		PaymentIn paymentIn = controller.getModel().getPaymentIn();
		assertNotNull("Payment should be loaded", paymentIn);
		assertEquals("PaymentIn status should be CARD_DETAILS_REQUIRED", PaymentStatus.CARD_DETAILS_REQUIRED, paymentIn.getStatus());

		//submit the data

		PaymentRequest paymentRequest = new PaymentRequest();
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentRequest.setAction(PaymentAction.MAKE_PAYMENT);
		paymentRequest.setNumber(DECLINED_CARD_NUMBER);
		paymentRequest.setName(CARD_HOLDER_NAME);
		paymentRequest.setCvv(CREDIT_CARD_CVV);
		paymentRequest.setMonth(VALID_EXPIRY_MONTH);
		paymentRequest.setYear(VALID_EXPIRY_YEAR);

		testEnv.getTestEnv().getPaymentProcessController(sessionId).processRequest(paymentRequest, paymentResponse);

		assertNotNull("response should not be empty", paymentResponse.getStatus());
		assertEquals(GetPaymentState.PaymentState.CHOOSE_ABANDON_OTHER, paymentResponse.getStatus());

		//parse the response result
		doc = testEnv.getTestEnv().getPageTester().renderPage("Payment/" + sessionId);
		Element paymentResultDiv = doc.getRootElement().getElementByAttributeValue("class", "pay-form");
		assertNotNull("Result div should be loaded", paymentResultDiv);
		Element failPaymentDiv = paymentResultDiv.getElementByAttributeValue("class", "pay-fail");
		assertNotNull("Failed payment div should be loaded", failPaymentDiv);
		Element output = failPaymentDiv.getElementByAttributeValue("class", "page-title");
		assertNotNull("Failed payment output should be loaded", output);
		assertFalse("Output should not be empty", output.isEmpty());
		assertTrue("Output should contain only 1 child", output.getChildren().size() == 1);
		Node failedMessage = output.getChildren().get(0);
		assertNotNull("Failed message should be included", failedMessage);
		assertEquals("Unexpected message", "This payment failed because the card was expired, invalid or does not have sufficient funds.",
				failedMessage.toString());

		//fire reverse invoice
		Element paymentResultForm = doc.getRootElement().getElementByAttributeValue("class", "submit-wrapper choose-wrapper");
		assertNotNull("Payment result form should be visible ", paymentResultForm);
		Element keepInvoiceButton = paymentResultForm.getElementById("pay-abandon");
		assertNotNull("Payment result form reverse invoice submit should be available", keepInvoiceButton);

		//submit the data
		paymentResponse = new PaymentResponse();
		paymentRequest = new PaymentRequest();
		paymentRequest.setAction(PaymentAction.CANCEL);
		testEnv.getTestEnv().getPaymentProcessController(sessionId).processRequest(paymentRequest, paymentResponse);
		assertNotNull("response should not be empty", paymentResponse.getStatus());
		assertEquals(GetPaymentState.PaymentState.FAILED, paymentResponse.getStatus());


		doc = testEnv.getTestEnv().getPageTester().renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);

		paymentResultDiv = doc.getRootElement().getElementByAttributeValue("class", "pay-form");
		assertNotNull("Result div should be loaded", paymentResultDiv);
		System.out.println(paymentResultDiv);
		failPaymentDiv = paymentResultDiv.getElementByAttributeValue("class", "pay-fail");
		assertNotNull("Failed payment div should be loaded", failPaymentDiv);
		output = failPaymentDiv.getElementByAttributeValue("class", "page-title");
		assertNotNull("Failed payment output should be loaded", output);
		assertFalse("Output should not be empty", output.isEmpty());
		assertTrue("Output should contain only 1 child", output.getChildren().size() == 1);
		failedMessage = output.getChildren().get(0);
		assertNotNull("Failed message should be included", failedMessage);
		assertEquals("Unexpected message", "This payment was cancelled.", failedMessage.toString());

//		assertTrue("Controller state should be final", controller.isFinalState());
	}


	protected void checkAsyncReplication(ObjectContext context) {
	}

	protected void checkProcessedResponse(GenericTransactionGroup transaction) throws Exception {
	}

	protected String checkResponseAndReceiveSessionId(GenericTransactionGroup transaction) {
		return null;
	}

	protected GenericParameterEntry createEntry(String name, String value) {
		GenericParameterEntry entry = PortHelper.createParameterEntry(SupportedVersions.V17);

		entry.setName(name);
		entry.setValue(value);

		return entry;
	}
}
