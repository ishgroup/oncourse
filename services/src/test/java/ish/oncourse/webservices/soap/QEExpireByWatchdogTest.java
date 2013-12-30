package ish.oncourse.webservices.soap;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.ProductStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.model.*;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.util.payment.PaymentProcessController.PaymentAction;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v6.stubs.replication.ArticleStub;
import ish.oncourse.webservices.v6.stubs.replication.MembershipStub;
import ish.oncourse.webservices.v6.stubs.replication.VoucherStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Node;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.services.Session;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class QEExpireByWatchdogTest extends QEPaymentProcess1_4CasesGUITest {

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
		assertTrue("InvoiceLines size should be 4", invoiceLines.size() == 4);
		for (InvoiceLine invoiceLine : invoiceLines) {
			List<InvoiceLineDiscount> discounts = invoiceLine.getInvoiceLineDiscounts();
			assertTrue("No discounts should be applied", discounts.size() == 0);
			if (invoiceLine.getAngelId().equals(1l)) {
				Enrolment enrolment = invoiceLine.getEnrolment();
				assertNotNull("Enrolment should not be empty", enrolment);
				assertEquals("Enrolment status should be in transaction", EnrolmentStatus.IN_TRANSACTION, enrolment.getStatus());
			} else if (invoiceLine.getAngelId().equals(2l)) {
				Membership membership = (Membership) invoiceLine.getProductItems().get(0);
				assertNotNull("Membership should not be empty", membership);
				assertEquals("Membership status should be new", ProductStatus.NEW, membership.getStatus());
			} else if (invoiceLine.getAngelId().equals(3l)) {
				Voucher voucher = (Voucher) invoiceLine.getProductItems().get(0);
				assertNotNull("Voucher should not be empty", voucher);
				assertEquals("Voucher status should be new", ProductStatus.NEW, voucher.getStatus());
			} else if (invoiceLine.getAngelId().equals(4l)) {
				Article article = (Article) invoiceLine.getProductItems().get(0);
				assertNotNull("Article should not be empty", article);
				assertEquals("Article status should be new", ProductStatus.NEW, article.getStatus());
			} else {
				assertFalse("Unexpected invoice line", true);
			}
		}

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

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		@SuppressWarnings("unchecked")
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 11 records.", 11, queuedRecords.size());
		int paymentsFound = 0, paymentLinesFound = 0, invoicesFound = 0, invoiceLinesFound = 0, enrolmentsFound = 0,
			membershipsFound = 0, vouchersFound = 0, articlesFound = 0;
		for (QueuedRecord record : queuedRecords) {
			if (PAYMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				paymentsFound++;
			} else if (PAYMENT_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				paymentLinesFound++;
			} else if (INVOICE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				invoicesFound++;
			} else if (INVOICE_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				invoiceLinesFound++;
			} else if (ENROLMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				enrolmentsFound++;
			} else if (MEMBERSHIP_IDENTIFIER.equals(record.getEntityIdentifier())){
				membershipsFound++;
			} else if (VOUCHER_IDENTIFIER.equals(record.getEntityIdentifier())){
				vouchersFound++;
			} else if (ARTICLE_IDENTIFIER.equals(record.getEntityIdentifier())){
				articlesFound++;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}

		assertEquals("Not all PaymentIns found in a queue", 1, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 1, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 1, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 4, invoiceLinesFound);
		assertEquals("Enrolment not found in a queue", 1, enrolmentsFound);
		assertEquals("Membership not found in a queue", 1, membershipsFound);
		assertEquals("Voucher not found in a queue", 1, vouchersFound);
		assertEquals("Article not found in a queue", 1, articlesFound);
	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("17 elements should be replicated for this payment", 17, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
				assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED, status);
			} else if (stub instanceof GenericEnrolmentStub) {
				if (stub.getAngelId() == 1l) {
					EnrolmentStatus status = EnrolmentStatus.valueOf(((GenericEnrolmentStub) stub).getStatus());
					assertEquals("Oncourse enrollment should be success", EnrolmentStatus.SUCCESS, status);
				} else {
					assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(),
						((GenericEnrolmentStub)stub).getStatus()), true);
				}
			} else if (stub instanceof VoucherStub) {
				assertEquals("Check of voucher status failed", ((VoucherStub) stub).getStatus(), ProductStatus.ACTIVE.getDatabaseValue());
			} else if (stub instanceof ArticleStub) {
				assertEquals("Check of article status failed", ((ArticleStub) stub).getStatus(), ProductStatus.ACTIVE.getDatabaseValue());
			} else if (stub instanceof MembershipStub) {
				assertEquals("Check of membership status failed", ((MembershipStub) stub).getStatus(), ProductStatus.ACTIVE.getDatabaseValue());
			}
		}
	}

	@Override
	protected String checkResponseAndReceiveSessionId(GenericTransactionGroup transaction) {
		String sessionId = null;
		assertEquals("17 stubs should be in response for this processing", 17, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
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
			} else if (MEMBERSHIP_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Membership status should not change after this processing",
					ProductStatus.NEW.getDatabaseValue(), ((ish.oncourse.webservices.v6.stubs.replication.MembershipStub)stub).getStatus());
			} else if (VOUCHER_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Voucher status should not change after this processing",
					ProductStatus.NEW.getDatabaseValue(), ((ish.oncourse.webservices.v6.stubs.replication.VoucherStub)stub).getStatus());
			} else if (ARTICLE_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Article status should not change after this processing",
					ProductStatus.NEW.getDatabaseValue(), ((ish.oncourse.webservices.v6.stubs.replication.ArticleStub)stub).getStatus());
			}
		}
		return sessionId;
	}

	@Test
	public void testExpireQEByWatchdog() throws Exception {
		//check that empty queuedRecords
		ObjectContext context = cayenneService.newNonReplicatingContext();
		checkQueueBeforeProcessing(context);
		authenticate();
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(getSupportedVersion());
		fillV6PaymentStubs(transaction);
		//process payment
		transaction = getPaymentPortType().processPayment(castGenericTransactionGroup(transaction));
		//check the response, validate the data and receive the sessionid
		String sessionId = checkResponseAndReceiveSessionId(transaction);
		checkQueueAfterProcessing(context);
		//check the status via service
		checkNotProcessedResponse(getPaymentStatus(sessionId));
		//call page processing
		testRenderPaymentPageForExpiration(sessionId);
		//check that async replication works correct
		checkAsyncReplication(context);
		//check the status via service when processing complete
		checkProcessedResponse(getPaymentStatus(sessionId));
	}

}
