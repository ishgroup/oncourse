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
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;

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
import org.junit.BeforeClass;
import org.junit.Test;

public class QEFailedPaymentReverseInvoiceTest extends RealWSTransportTest {
	private static TestServer server;

	@Override
	protected TestServer getServer() {
		return server;
	}

	@BeforeClass		
	public static void initTestServer() throws Exception {
		server = startRealWSServer(9095);
	}
	
	private void testRenderPaymentPageWithReverseInvoice(String sessionId) {
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
		
		Map<String, String> fieldValues = new HashMap<>();
		fieldValues.put(cardName.getAttribute(ID_ATTRIBUTE), CARD_HOLDER_NAME);
		fieldValues.put(cardNumber.getAttribute(ID_ATTRIBUTE), DECLINED_CARD_NUMBER);
		fieldValues.put(cardCVV.getAttribute(ID_ATTRIBUTE), CREDIT_CARD_CVV);
		fieldValues.put(expirityMonth.getAttribute(ID_ATTRIBUTE), VALID_EXPIRITY_MONTH);
		fieldValues.put(expirityYear.getAttribute(ID_ATTRIBUTE), VALID_EXPIRITY_YEAR);
		fieldValues.put("cardTypeField", CreditCardType.VISA.getDisplayName());
		
		//get session to check that processing in progress
		Session session = serviceTest.getService(TestableRequest.class).getSession(false);
		assertNotNull("Session should be inited for controller", session);
		PaymentProcessController controller = (PaymentProcessController) session.getAttribute("state:Payment::paymentProcessController");
		assertNotNull("controller should be not empty", controller);
		//load the payment structure 
		PaymentIn paymentIn = controller.getPaymentIn();
		assertNotNull("Payment should be loaded", paymentIn);
		assertEquals("PaymentIn status should be in transaction", PaymentStatus.IN_TRANSACTION, paymentIn.getStatus());
		List<PaymentInLine> paymentInLines = paymentIn.getPaymentInLines();
		assertTrue("PaymentInLines size should be 1", paymentInLines.size() == 1);
		Invoice invoice = paymentInLines.get(0).getInvoice();
		assertNotNull("Invoice should not be empty", invoice);
		List<InvoiceLine> invoiceLines = invoice.getInvoiceLines();
		assertTrue("InvoiceLines size should be 1", invoiceLines.size() == 1);
		List<InvoiceLineDiscount> discounts = invoiceLines.get(0).getInvoiceLineDiscounts();
		assertTrue("No discounts should be applied", discounts.size() == 0);
		Enrolment enrolment = invoiceLines.get(0).getEnrolment();
		assertNotNull("Enrolment should not be empty", enrolment);
		assertEquals("Enrolment status should be in transaction", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
		
		//submit the data
		TestableResponse response = tester.clickSubmitAndReturnResponse(submitButton, fieldValues);
		assertNotNull("response should not be empty", response);
		System.out.println(response.getRedirectURL());
		
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
		Element paymentResultForm = doc.getElementById("paymentResultForm");
		assertNotNull("Payment result form should be visible ", paymentResultForm);
		Element reverseInvoiceButton = paymentResultForm.getElementById("abandonReverse");
		assertNotNull("Payment result form reverse invoice submit should be available", reverseInvoiceButton);
		
		//submit the data
		response = tester.clickSubmitAndReturnResponse(reverseInvoiceButton, new HashMap<String, String>());
		assertNotNull("response should not be empty", response);
		System.out.println(response.getRedirectURL());
		
		//start wait in loop for response
		while (!controller.isFinalState()) {
			try {
				//sleep for 5 seconds to have some time for processing
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			doc = tester.renderPage("Payment/" + sessionId);
			assertNotNull("Document should be loaded", doc);
		}
		
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
		
		assertTrue("Controller state should be final", controller.isFinalState());
		
	}
	
	@Test
	public void testQEReverseInvoice() throws Exception {
		//check that empty queuedRecords
		ObjectContext context = cayenneService.newNonReplicatingContext();
		assertTrue("Queue should be empty before processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		authenticate();
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(getSupportedVersion());
		fillV4PaymentStubsForCases1_4(transaction);		
		//process payment
		transaction = getPaymentPortType().processPayment(castGenericTransactionGroup(transaction));
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
		testRenderPaymentPageWithReverseInvoice(sessionId);
		//check that async replication works correct
		@SuppressWarnings("unchecked")
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 12 records.", 12, queuedRecords.size());
		int isPaymentFound = 0, isPaymentLineFound = 0, isInvoiceFound = 0, isInvoiceLineFound = 0, isEnrolmentFound = 0, isContactFound = 0, 
			isStudentFound = 0;
		for (QueuedRecord record : queuedRecords) {
			if (PAYMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 2 paymentIn should exist in a queue", isPaymentFound >= 2);
				isPaymentFound++;
			} else if (PAYMENT_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 3 paymentInLine should exist in a queue", isPaymentLineFound >= 3);
				isPaymentLineFound++;
			} else if (INVOICE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 2 invoice should exist in a queue", isInvoiceFound >= 2);
				isInvoiceFound++;
			} else if (INVOICE_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 2 invoiceLine should exist in a queue", isInvoiceLineFound >= 2);
				isInvoiceLineFound++;
			} else if (ENROLMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 enrolment should exist in a queue", isEnrolmentFound >= 1);
				isEnrolmentFound++;
			} else if (CONTACT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 contact should exist in a queue", isContactFound >= 1);
				isContactFound++;
			} else if (STUDENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 student should exist in a queue", isStudentFound >= 1);
				isStudentFound++;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}
		assertEquals("Not all PaymentIns found in a queue", 2, isPaymentFound);
		assertEquals("Not all PaymentInLines found in a queue", 3, isPaymentLineFound);
		assertEquals("Not all Invoices found in a queue", 2, isInvoiceFound);
		assertEquals("Not all InvoiceLines found in a  queue", 2, isInvoiceLineFound);
		assertEquals("Enrolment not found in a queue", 1, isEnrolmentFound);
		assertEquals("Contact not found in a queue", 1, isContactFound);
		assertEquals("Student not found in a queue", 1, isStudentFound);
		
		//check the status via service when processing complete
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertFalse("Get status call should not return empty response for payment in final status", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("16 elements should be replicated for this payment", 16, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				if (stub.getWillowId() == 1l) {
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED_CARD_DECLINED, status);
				} else if (stub.getWillowId() == 2l) {
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be failed after expiration", PaymentStatus.SUCCESS, status);
				} else {
					assertFalse(String.format("Unexpected PaymentIn with id= %s and status= %s found in a queue", stub.getWillowId(), 
						((GenericPaymentInStub) stub).getStatus()), true);
				}
			}
		}
		logout();
	}

}
