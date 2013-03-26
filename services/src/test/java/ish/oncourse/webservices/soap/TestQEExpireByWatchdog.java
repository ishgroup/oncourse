package ish.oncourse.webservices.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
import ish.oncourse.util.payment.PaymentProcessController.PaymentAction;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.replication.services.SupportedVersions;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Node;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.services.Session;
import org.junit.Test;

public class TestQEExpireByWatchdog extends RealWSTransportTest {
	
	@Override
	protected void initTestServer() throws Exception {
		server = startRealWSServer(9092);
	}
	
	private void testRenderPaymentPageForExpiration(String sessionId) {
		assertNotNull("Session id should not be null", sessionId);
		Document doc = tester.renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);
		
		//get session to check that processing in progress
		Session session = serviceTest.getService(TestableRequest.class).getSession(false);
		assertNotNull("Session should be inited for controller", session);
		PaymentProcessController controller = (PaymentProcessController) session.getAttribute("state:Payment::paymentProcessController");
		assertNotNull("controller should be not empty", controller);
		//load the payment structure till expiration
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
		//expire the payment
		controller.processAction(PaymentAction.EXPIRE_PAYMENT);
		//reload the page to receive the cancel payment message
		doc = tester.renderPage("Payment/" + sessionId);
		assertNotNull("Document should be loaded", doc);
		assertTrue("Controller state should be expired", controller.isExpired());
		
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
		assertEquals("Unexpected message", "This payment was cancelled.", failedMessage.toString());
	}
	
	@Test
	public void testExpireQEByWatchdog() throws Exception {
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
		fillV4PaymentStubs(transaction);
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
		testRenderPaymentPageForExpiration(sessionId);
		//check that async replication works correct
		@SuppressWarnings("unchecked")
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertTrue("Queue should contain 5 records.", queuedRecords.size() == 5 );
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
		assertTrue("11 elements should be replicated for this payment", transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size() == 11);
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
				assertTrue("Payment status should be failed after expiration", PaymentStatus.FAILED.equals(status));
			}
		}
		//logout
		getReplicationPortType().logout(getCommunicationKey());
	}

}
