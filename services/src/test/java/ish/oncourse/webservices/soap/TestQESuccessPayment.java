package ish.oncourse.webservices.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import ish.common.types.CreditCardType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.replication.services.SupportedVersions;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Node;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.internal.test.TestableResponse;
import org.apache.tapestry5.services.Session;
import org.junit.Test;

public class TestQESuccessPayment extends RealWSTransportTest {
	
	@Override
	protected void initTestServer() throws Exception {
		server = startRealWSServer(9094);
	}
	
	private void testRenderPaymentPage(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = tester.renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);
		
		Element paymentForm = doc.getElementById("paymentDetailsForm");
		assertNotNull("Payment form should be visible ", paymentForm);
		Element cardName = paymentForm.getElementById("cardName");
		assertNotNull("Card name input should be available", cardName);
		Element cardNumber = paymentForm.getElementById("cardnumber");
		assertNotNull("Card number input should be available", cardNumber);
		Element expirityMonth = paymentForm.getElementById("expiryMonth");
		assertNotNull("Card expirity month input should be available", expirityMonth);
		Element expirityYear = paymentForm.getElementById("expiryYear");
		assertNotNull("Card expirity year input should be available", expirityYear);
		Element cardCVV = paymentForm.getElementById("cardcvv");
		assertNotNull("Card CVV input should be available", cardCVV);
		Element submitButton = paymentForm.getElementById("paymentSubmit");
		assertNotNull("Payment form submit should be available", submitButton);
		
		Map<String, String> fieldValues = new HashMap<String, String>();
		fieldValues.put(cardName.getAttribute(ID_ATTRIBUTE), CARD_HOLDER_NAME);
		fieldValues.put(cardNumber.getAttribute(ID_ATTRIBUTE), VALID_CARD_NUMBER);
		fieldValues.put(cardCVV.getAttribute(ID_ATTRIBUTE), CREDIT_CARD_CVV);
		fieldValues.put(expirityMonth.getAttribute(ID_ATTRIBUTE), VALID_EXPIRITY_MONTH);
		fieldValues.put(expirityYear.getAttribute(ID_ATTRIBUTE), VALID_EXPIRITY_YEAR);
		fieldValues.put("cardTypeField", CreditCardType.VISA.getDisplayName());
		
		//submit the data
		TestableResponse response = tester.clickSubmitAndReturnResponse(submitButton, fieldValues);
		assertNotNull("response should not be empty", response);
		System.out.println(response.getRedirectURL());
		
		//get session to check that processing in progress
		Session session = serviceTest.getService(TestableRequest.class).getSession(false);
		assertNotNull("Session should be inited for controller", session);
		PaymentProcessController controller = (PaymentProcessController) session.getAttribute("state:Payment::paymentProcessController");
		assertNotNull("controller should be not empty", controller);
		
		//start wait in loop for response
		while (controller.isProcessingState()) {
			try {
				//sleep for 5 seconds to have some time for processing
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			doc = tester.renderPage("Payment/" + sessionId);
			assertNotNull("Document should be loaded", doc);
		}
		
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
	
	@Test
	public void testSuccessQE() throws Exception {
		//check that empty queuedRecords
		ObjectContext context = cayenneService.newNonReplicatingContext();
		assertTrue("Queue should be empty before processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		//authenticate first
		Long oldCommunicationKey = getCommunicationKey();
		Long newCommunicationKey = getReplicationPortType().authenticate(getSecurityCode(), oldCommunicationKey);
		assertNotNull("Received communication key should not be empty", newCommunicationKey);
		assertTrue("Communication keys should be different before and after authenticate call", oldCommunicationKey.compareTo(newCommunicationKey) != 0);
		assertTrue("New communication key should be equal to actual", newCommunicationKey.compareTo(getCommunicationKey()) == 0);
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(SupportedVersions.V4);
		fillV4PaymentStubsForCases1_4(transaction);
		//process payment
		transaction = getPaymentPortType().processPayment((TransactionGroup) transaction);
		//check the response, validate the data and receive the sessionid
		String sessionId = null;
		assertEquals("11 stubs should be in response for this processing", 11, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			assertNotNull("Willowid after the first payment processing should not be NULL", stub.getWillowId());
			if (PAYMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				GenericPaymentInStub payment = (GenericPaymentInStub) stub;
				assertNull("Only 1 paymentIn entity should exist in response. This entity sessionid will be used for page processing", sessionId);
				sessionId = payment.getSessionId();
				assertNotNull("PaymentIn entity should contain the sessionid after processing", sessionId);
				assertEquals("Payment status should not change after this processing", PaymentStatus.IN_TRANSACTION.getDatabaseValue(), payment.getStatus());
			} else if (ENROLMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Enrolment status should not change after this processing", EnrolmentStatus.IN_TRANSACTION.name(), 
					((GenericEnrolmentStub) stub).getStatus());
			}
		}
		assertTrue("Queue should be empty after processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		//check the status via service
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertTrue("Get status call should return empty response for in transaction payment", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		//call page processing
		testRenderPaymentPage(sessionId);
		//check that async replication works correct
		@SuppressWarnings("unchecked")
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 5 records.", 5, queuedRecords.size());
		boolean isPaymentFound = false, isPaymentLineFound = false, isInvoiceFound = false, isInvoiceLineFound = false, isEnrolmentDound = false;
		for (QueuedRecord record : queuedRecords) {
			if (PAYMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 paymentIn should exist in a queue", isPaymentFound);
				isPaymentFound = true;
			} else if (PAYMENT_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 paymentInLine should exist in a queue", isPaymentLineFound);
				isPaymentLineFound = true;
			} else if (INVOICE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 invoice should exist in a queue", isInvoiceFound);
				isInvoiceFound = true;
			} else if (INVOICE_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 invoiceLine should exist in a queue", isInvoiceLineFound);
				isInvoiceLineFound = true;
			} else if (ENROLMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 enrolment should exist in a queue", isEnrolmentDound);
				isEnrolmentDound = true;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}
		assertTrue("Payment not found in a queue", isPaymentFound);
		assertTrue("Payment line not found in a queue", isPaymentLineFound);
		assertTrue("Invoice not found in a queue", isInvoiceFound);
		assertTrue("Invoice line not found in a  queue", isInvoiceLineFound);
		assertTrue("Enrolment not found in a queue", isEnrolmentDound);
		//check the status via service when processing complete
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertFalse("Get status call should not return empty response for payment in final status", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("11 elements should be replicated for this payment", 11, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				if (stub.getWillowId() == 1l) {
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be failed after expiration", PaymentStatus.SUCCESS, status);
				} else {
					assertFalse(String.format("Unexpected PaymentIn with id= %s and status= %s found in a queue", stub.getWillowId(), 
						((GenericPaymentInStub) stub).getStatus()), true);
				}
			}
		}
		//logout
		getReplicationPortType().logout(getCommunicationKey());
	}
}
