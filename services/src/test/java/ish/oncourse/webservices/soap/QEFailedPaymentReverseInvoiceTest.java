package ish.oncourse.webservices.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import ish.common.types.*;
import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.util.*;

import java.math.BigDecimal;
import java.util.List;

import ish.oncourse.webservices.v6.stubs.replication.ArticleStub;
import ish.oncourse.webservices.v6.stubs.replication.InvoiceStub;
import ish.oncourse.webservices.v6.stubs.replication.MembershipStub;
import ish.oncourse.webservices.v6.stubs.replication.VoucherStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.junit.BeforeClass;
import org.junit.Test;

public class QEFailedPaymentReverseInvoiceTest extends QEPaymentProcess1_4CasesGUITest {
	private static TestServer server;

	@Override
	protected TestServer getServer() {
		return server;
	}

	@BeforeClass		
	public static void initTestServer() throws Exception {
		server = startRealWSServer(QE_FAILED_PAYMENT_REVERSE_INVOICE_TEST_PORT);
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

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		@SuppressWarnings("unchecked")
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 21 records.", 21, queuedRecords.size());
		int paymentsFound = 0, paymentLinesFound = 0, invoicesFound = 0, invoiceLinesFound = 0, enrolmentsFound = 0,
			contactsFound = 0, studentsFound = 0, membershipsFound = 0, vouchersFound = 0, articlesFound = 0;
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
			} else if (CONTACT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				contactsFound++;
			} else if (STUDENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				studentsFound++;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}
		assertEquals("Not all PaymentIns found in a queue", 2, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 3, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 2, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 8, invoiceLinesFound);
		assertEquals("Enrolment not found in a queue", 1, enrolmentsFound);
		assertEquals("Membership not found in a queue", 1, membershipsFound);
		assertEquals("Voucher not found in a queue", 1, vouchersFound);
		assertEquals("Article not found in a queue", 1, articlesFound);
		assertEquals("Contact not found in a queue", 1, contactsFound);
		assertEquals("Student not found in a queue", 1, studentsFound);
	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("25 elements should be replicated for this payment", 25, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
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
			} else if (stub instanceof GenericEnrolmentStub) {
				if (stub.getAngelId() == 1l) {
					EnrolmentStatus status = EnrolmentStatus.valueOf(((GenericEnrolmentStub) stub).getStatus());
					assertEquals("Oncourse enrollment should be failed after reverse", EnrolmentStatus.FAILED, status);
				} else {
					assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(),
						((GenericEnrolmentStub)stub).getStatus()), true);
				}
			} else if (stub instanceof GenericInvoiceStub) {
				if (stub.getAngelId() == null && stub.getWillowId() == 2l) {
					assertEquals("Incorrect reverse invoice amount", ((InvoiceStub) stub).getTotalGst(), new BigDecimal("-100.00"));
				} else if (stub.getAngelId().equals(1l)) {
					assertEquals("Incorrect original invoice amount", ((InvoiceStub) stub).getTotalGst(), new BigDecimal("100.00"));
				}  else {
					assertFalse(String.format("Unexpected Invoice with id= %s and angelid= %s found in a queue",
						stub.getWillowId(), stub.getAngelId()), true);
				}
			} else if (stub instanceof VoucherStub) {
				assertEquals("Check of voucher status failed", ((VoucherStub) stub).getStatus(), ProductStatus.CANCELLED.getDatabaseValue());
			} else if (stub instanceof ArticleStub) {
				assertEquals("Check of article status failed", ((ArticleStub) stub).getStatus(), ProductStatus.CANCELLED.getDatabaseValue());
			} else if (stub instanceof MembershipStub) {
				assertEquals("Check of membership status failed", ((MembershipStub) stub).getStatus(), ProductStatus.CANCELLED.getDatabaseValue());
			}
		}
	}

	@Test
	public void testQEReverseInvoice() throws Exception {
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
		testRenderPaymentPageWithReverseInvoice(sessionId);
		//check that async replication works correct
		checkAsyncReplication(context);
		
		//check the status via service when processing complete
		checkProcessedResponse(getPaymentStatus(sessionId));
		logout();
	}

}
