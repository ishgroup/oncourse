package ish.oncourse.webservices.soap.v16;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.services.payment.*;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder;
import ish.oncourse.webservices.util.GenericParameterEntry;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Node;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Use cases described in squish task
 * https://squish.ish.com.au/tasks/17142
 */
public abstract class QEPaymentProcessTest extends RealWSTransportTest {

	private NewPaymentProcessController getController(String sessionId) {
		return new PaymentControllerBuilder(sessionId,
				serviceTest.getService(INewPaymentGatewayServiceBuilder.class),
				cayenneService,
				messages,
				serviceTest.getService(TestableRequest.class)).build();
	}


	protected final void testRenderPaymentPageWithReverseInvoice(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = tester.renderPage("Payment/" + sessionId);
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
		Request request = serviceTest.getService(TestableRequest.class);
		Session session = request.getSession(false);
		assertNull("Session should be null", session);

		NewPaymentProcessController controller = getController(sessionId);

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
		paymentRequest.setMonth(VALID_EXPIRITY_MONTH);
		paymentRequest.setYear(VALID_EXPIRITY_YEAR);

		getController(sessionId).processRequest(paymentRequest, paymentResponse);

		assertNotNull("response should not be empty", paymentResponse.getStatus());
		assertEquals(GetPaymentState.PaymentState.CHOOSE_ABANDON_OTHER, paymentResponse.getStatus());

		//parse the response result
		doc = tester.renderPage("Payment/" + sessionId);
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
		getController(sessionId).processRequest(paymentRequest, paymentResponse);
		assertNotNull("response should not be empty", paymentResponse.getStatus());
		assertEquals(GetPaymentState.PaymentState.FAILED, paymentResponse.getStatus());


		doc = tester.renderPage("Payment/" + sessionId);
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

	protected final void renderPaymentPageWithKeepInvoiceProcessing(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = tester.renderPage("Payment/" + sessionId);
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

		PaymentRequest paymentRequest = new PaymentRequest();
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentRequest.setAction(PaymentAction.MAKE_PAYMENT);
		paymentRequest.setNumber(DECLINED_CARD_NUMBER);
		paymentRequest.setName(CARD_HOLDER_NAME);
		paymentRequest.setCvv(CREDIT_CARD_CVV);
		paymentRequest.setMonth(VALID_EXPIRITY_MONTH);
		paymentRequest.setYear(VALID_EXPIRITY_YEAR);


		NewPaymentProcessController controller = getController(sessionId);

		PaymentIn paymentIn = controller.getModel().getPaymentIn();
		assertNotNull("Payment should be loaded", paymentIn);
		assertEquals("PaymentIn status should be CARD_DETAILS_REQUIRED", PaymentStatus.CARD_DETAILS_REQUIRED, paymentIn.getStatus());

		//get session to check that processing in progress
		Session session = serviceTest.getService(TestableRequest.class).getSession(false);
		assertNull("Session should be null", session);

		controller.processRequest(paymentRequest, paymentResponse);
		//parse the response result
		doc = tester.renderPage("Payment/" + sessionId);
		assertNotNull("response should not be empty", paymentResponse.getStatus());
		assertEquals(GetPaymentState.PaymentState.CHOOSE_ABANDON_OTHER, paymentResponse.getStatus());

		//parse the response result
		Element paymentResultDiv = doc.getRootElement().getElementByAttributeValue("class", "pay-form");
		assertNotNull("Result div should be loaded", paymentResultDiv);
		System.out.println(paymentResultDiv);
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

		//fire keep invoice
		Element paymentResultForm = doc.getRootElement().getElementByAttributeValue("class", "submit-wrapper choose-wrapper");
		assertNotNull("Payment result form should be visible ", paymentResultForm);
		Element keepInvoiceButton = paymentResultForm.getElementById("pay-abandon");
		assertNotNull("Payment result form keep invoice submit should be available", keepInvoiceButton);

		paymentRequest = new PaymentRequest();
		paymentRequest.setAction(PaymentAction.CANCEL);
		paymentResponse = new PaymentResponse();
		getController(sessionId).processRequest(paymentRequest, paymentResponse);

		doc = tester.renderPage("Payment/" + sessionId);
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

		assertEquals("Controller state should be final", GetPaymentState.PaymentState.FAILED, paymentResponse.getStatus());
	}

	protected final void renderPaymentPageWithSuccessProcessing(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = tester.renderPage("Payment/" + sessionId);
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

		Map<String, String> fieldValues = new HashMap<>();
		fieldValues.put(cardName.getAttribute(ID_ATTRIBUTE), CARD_HOLDER_NAME);
		fieldValues.put(cardNumber.getAttribute(ID_ATTRIBUTE), VALID_CARD_NUMBER);
		fieldValues.put(cardCVV.getAttribute(ID_ATTRIBUTE), CREDIT_CARD_CVV);
		fieldValues.put(expirityMonth.getAttribute(ID_ATTRIBUTE), VALID_EXPIRITY_MONTH);
		fieldValues.put(expirityYear.getAttribute(ID_ATTRIBUTE), VALID_EXPIRITY_YEAR);
		fieldValues.put("cardTypeField", CreditCardType.VISA.getDisplayName());


		PaymentRequest paymentRequest = new PaymentRequest();
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentRequest.setAction(PaymentAction.MAKE_PAYMENT);
		paymentRequest.setNumber(VALID_CARD_NUMBER);
		paymentRequest.setName(CARD_HOLDER_NAME);
		paymentRequest.setCvv(CREDIT_CARD_CVV);
		paymentRequest.setMonth(VALID_EXPIRITY_MONTH);
		paymentRequest.setYear(VALID_EXPIRITY_YEAR);

		getController(sessionId).processRequest(paymentRequest, paymentResponse);
		assertNotNull("response should not be empty", paymentResponse.getStatus());
		assertEquals(GetPaymentState.PaymentState.SUCCESS, paymentResponse.getStatus());
		doc = tester.renderPage("Payment/" + sessionId);
		//parse the response result
		Element paymentResultDiv = doc.getRootElement().getElementByAttributeValue("class", "pay-form");
		assertNotNull("Result div should be loaded", paymentResultDiv);
		System.out.println(paymentResultDiv);
		Element successPaymentDiv = paymentResultDiv.getElementByAttributeValue("class", "pay-success");
		assertNotNull("Success payment div should be loaded", successPaymentDiv);
		System.out.println(successPaymentDiv);
		Element output = successPaymentDiv.getElementByAttributeValue("class", "page-title");
		assertNotNull("Success payment output should be loaded", output);
		//System.out.println(output);
		assertFalse("Output should not be empty", output.isEmpty());
		assertTrue("Output should contain only 1 child", output.getChildren().size() == 1);
		Node successMessage = output.getChildren().get(0);
		assertNotNull("Success message should be included", successMessage);
		//System.out.println(successMessage);
		assertEquals("Unexpected message", "Payment was successful.", successMessage.toString());
	}


	protected final void checkQueueBeforeProcessing(ObjectContext context) {
		assertTrue("Queue should be empty before processing", ObjectSelect.query(QueuedRecord.class).select(context).isEmpty());
	}

	protected final void checkQueueAfterProcessing(ObjectContext context) {
		List<QueuedRecord> queuedRecords = ObjectSelect.query(QueuedRecord.class)
				.select(context);

		//Set up sessionId for Invoice.
		//only Invoices can be added to replication queue
		assertEquals("Invoice is not found in a queue", queuedRecords.size(), QueuedRecord.ENTITY_IDENTIFIER.eq(INVOICE_IDENTIFIER).filterObjects(queuedRecords).size());
	}

	protected final GenericTransactionGroup getPaymentStatus(String sessionId) throws Exception {
		return getPaymentPortType().getPaymentStatus(sessionId);
	}

	protected final void checkNotProcessedResponse(GenericTransactionGroup transaction) {
		assertTrue("Get status call should return empty response for in transaction payment",
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
	}

	protected void checkAsyncReplication(ObjectContext context) {}

	protected void checkProcessedResponse(GenericTransactionGroup transaction) throws Exception {
	}

	protected String checkResponseAndReceiveSessionId(GenericTransactionGroup transaction) {
		return null;
	}

	protected GenericParameterEntry createEntry(String name, String value) {
		GenericParameterEntry entry = PortHelper.createParameterEntry(SupportedVersions.V16);

		entry.setName(name);
		entry.setValue(value);

		return entry;
	}
}
