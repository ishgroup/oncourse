package ish.oncourse.webservices.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.ProductStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.junit.BeforeClass;
import org.junit.Test;

public class QEFailedPaymentKeepInvoiceTest extends QEPaymentProcess1_4CasesGUITest {
	private static TestServer server;

	@Override
	protected TestServer getServer() {
		return server;
	}

	@BeforeClass	
	public static void initTestServer() throws Exception {
		server = startRealWSServer(QE_FAILED_PAYMENT_KEEP_INVOICE_TEST_PORT);
	}
	
	@Test
	public void testQEKeepInvoice() throws Exception {
		//check that empty queuedRecords
		ObjectContext context = cayenneService.newNonReplicatingContext();
		assertTrue("Queue should be empty before processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		authenticate();
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(getSupportedVersion());
		fillV6PaymentStubs(transaction);
		//process payment
		transaction = getPaymentPortType().processPayment(castGenericTransactionGroup(transaction));
		//check the response, validate the data and receive the sessionid
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
		assertTrue("Queue should be empty after processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		//check the status via service
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertTrue("Get status call should return empty response for in transaction payment", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		//call page processing
		renderPaymentPageWithKeepInvoiceProcessing(sessionId);
		//check that async replication works correct
		@SuppressWarnings("unchecked")
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 11 records.", 11, queuedRecords.size());
		int invoiceLineCount = 0;
		boolean isPaymentFound = false, isPaymentLineFound = false, isInvoiceFound = false, isEnrolmentFound = false,
				isMembershipFound = false, isVoucherFound = false, isArticleFound = false;
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
				assertFalse("Only 4 invoiceLines should exist in a queue", invoiceLineCount > 3);
				invoiceLineCount++;
			} else if (ENROLMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 enrolment should exist in a queue", isEnrolmentFound);
				isEnrolmentFound = true;
			} else if (MEMBERSHIP_IDENTIFIER.equals(record.getEntityIdentifier())){
				assertFalse("Only 1 membership should exist in a queue", isMembershipFound);
				isMembershipFound = true;
			} else if (VOUCHER_IDENTIFIER.equals(record.getEntityIdentifier())){
				assertFalse("Only 1 voucher should exist in a queue", isVoucherFound);
				isVoucherFound = true;
			} else if (ARTICLE_IDENTIFIER.equals(record.getEntityIdentifier())){
				assertFalse("Only 1 article should exist in a queue", isArticleFound);
				isArticleFound = true;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}
		assertTrue("Payment not found in a queue", isPaymentFound);
		assertTrue("Payment line not found in a queue", isPaymentLineFound);
		assertTrue("Invoice not found in a queue", isInvoiceFound);
		assertTrue("Not all Invoice lines exists in a queue", invoiceLineCount == 4);
		assertTrue("Enrolment not found in a queue", isEnrolmentFound);
		assertTrue("Membership not found in a queue", isMembershipFound);
		assertTrue("Voucher not found in a queue", isVoucherFound);
		assertTrue("Article not found in a queue", isArticleFound);
		//check the status via service when processing complete
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertFalse("Get status call should not return empty response for payment in final status", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("17 elements should be replicated for this payment", 17, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
				assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED_CARD_DECLINED, status);
			}
		}
		logout();
	}

}
