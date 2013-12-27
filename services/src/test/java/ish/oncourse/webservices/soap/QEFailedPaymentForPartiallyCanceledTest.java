package ish.oncourse.webservices.soap;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.ProductStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericInvoiceStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;

import java.util.List;

import ish.oncourse.webservices.v6.stubs.replication.ArticleStub;
import ish.oncourse.webservices.v6.stubs.replication.MembershipStub;
import ish.oncourse.webservices.v6.stubs.replication.VoucherStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;

public class QEFailedPaymentForPartiallyCanceledTest extends RealWSTransportTest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEProcessCase8Dataset.xml";
	private static TestServer server;

	@Override
	protected TestServer getServer() {
		return server;
	}

	@BeforeClass	
	public static void initTestServer() throws Exception {
		server = startRealWSServer(QE_FAILED_PAYMENT_FOR_PARTIALLY_CANCELED_TEST_PORT);
	}
	
	protected String getDataSetFile() {
		return DEFAULT_DATASET_XML;
	}
	
	@Test
	public void testQEKeepInvoice() throws Exception {
		//check that empty queuedRecords
		ObjectContext context = cayenneService.newNonReplicatingContext();
		assertTrue("Queue should be empty before processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
		authenticate();
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(getSupportedVersion());
		fillV6PaymentStubsForCases8(transaction);
		//process payment
		transaction = getPaymentPortType().processPayment(castGenericTransactionGroup(transaction));
		//check the response, validate the data and receive the sessionid
		String sessionId = null;
		assertEquals("19 stubs should be in response for this processing", 19, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			assertNotNull("Willowid after the first payment processing should not be NULL", stub.getWillowId());
			if (PAYMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				GenericPaymentInStub payment = (GenericPaymentInStub) stub;
				assertNull("Only 1 paymentIn entity should exist in response. This entity sessionid will be used for page processing", sessionId);
				sessionId = payment.getSessionId();
				assertNotNull("PaymentIn entity should contain the sessionid after processing", sessionId);
				assertEquals("Payment status should not change after this processing", PaymentStatus.IN_TRANSACTION.getDatabaseValue(), payment.getStatus());
			} else if (ENROLMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				if (stub.getAngelId() == 10l) {
					assertEquals("Enrolment status should not change after this processing", EnrolmentStatus.SUCCESS.name(), 
						((GenericEnrolmentStub) stub).getStatus());
				} else if (stub.getAngelId() == 11l) {
					assertEquals("Enrolment status should not change after this processing", EnrolmentStatus.CANCELLED.name(), 
						((GenericEnrolmentStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected enrolment stub with angelid= %s and willowid= %s", stub.getAngelId(), stub.getWillowId()), true);
				}
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
		assertEquals("Queue should contain 13 records.", 13, queuedRecords.size());
		int isPaymentFound = 0, isPaymentLineFound = 0, isInvoiceFound = 0, isInvoiceLineFound = 0,
			isEnrolmentFound = 0, isMembershipFound = 0, isVoucherFound = 0, isArticleFound = 0;
		for (QueuedRecord record : queuedRecords) {
			if (PAYMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 paymentIn should exist in a queue", isPaymentFound >= 1);
				isPaymentFound++;
			} else if (PAYMENT_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 2 paymentInLine should exist in a queue", isPaymentLineFound >= 1);
				isPaymentLineFound++;
			} else if (INVOICE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 invoice should exist in a queue", isInvoiceFound >= 1);
				isInvoiceFound++;
			} else if (INVOICE_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 5 invoiceLine should exist in a queue", isInvoiceLineFound >= 5);
				isInvoiceLineFound++;
			} else if (ENROLMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("Only 1 enrolment should exist in a queue", isEnrolmentFound>=2);
				isEnrolmentFound++;
			} else if (MEMBERSHIP_IDENTIFIER.equals(record.getEntityIdentifier())){
				assertFalse("Only 1 membership should exist in a queue", isMembershipFound >= 1);
				isMembershipFound++;
			} else if (VOUCHER_IDENTIFIER.equals(record.getEntityIdentifier())){
				assertFalse("Only 1 voucher should exist in a queue", isVoucherFound >= 1);
				isVoucherFound++;
			} else if (ARTICLE_IDENTIFIER.equals(record.getEntityIdentifier())){
				assertFalse("Only 1 article should exist in a queue", isArticleFound >= 1);
				isArticleFound++;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}
		assertEquals("Not all PaymentIns found in a queue", 1, isPaymentFound);
		assertEquals("Not all PaymentInLines found in a queue", 1, isPaymentLineFound);
		assertEquals("Not all Invoices found in a queue", 1, isInvoiceFound);
		assertEquals("Not all InvoiceLines found in a  queue", 5, isInvoiceLineFound);
		assertEquals("Not all Enrolments found in a  queue", 2, isEnrolmentFound);
		assertEquals("Membership not found in a queue", 1, isMembershipFound);
		assertEquals("Voucher not found in a queue", 1, isVoucherFound);
		assertEquals("Article not found in a queue", 1, isArticleFound);
		
		//check the status via service when processing complete
		transaction = getPaymentPortType().getPaymentStatus(sessionId);
		assertFalse("Get status call should not return empty response for payment in final status", 
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("19 elements should be replicated for this payment", 19, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				if (stub.getAngelId() == 1l) {
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED_CARD_DECLINED, status);
				} else {
					assertFalse(String.format("Unexpected PaymentIn with id= %s and status= %s found in a queue", stub.getWillowId(), 
						((GenericPaymentInStub) stub).getStatus()), true);
				}
			} else if (stub instanceof GenericEnrolmentStub) {
				if (stub.getAngelId() == 10l) {
					EnrolmentStatus status = EnrolmentStatus.valueOf(((GenericEnrolmentStub) stub).getStatus());
					assertEquals("Oncourse enrollment should be success after expiration", EnrolmentStatus.SUCCESS, status);
				} else if (stub.getAngelId() == 11l) {
					EnrolmentStatus status = EnrolmentStatus.valueOf(((GenericEnrolmentStub) stub).getStatus());
					assertEquals("Oncourse enrollment should be refunded after expiration", EnrolmentStatus.CANCELLED, status);
				} else {
					assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(), 
						((GenericEnrolmentStub)stub).getStatus()), true);
				}
			} else if (stub instanceof GenericInvoiceStub) {
				if (stub.getAngelId() != 10l) {
					assertFalse(String.format("Unexpected invoice stub with willowid= %s and angelid= %s found in a queue", 
						stub.getWillowId(), stub.getAngelId()), true);
				}
			} else if (INVOICE_LINE_IDENTIFIER.equals(stub.getEntityIdentifier())) {
					if ((stub.getAngelId() != 10l) && (stub.getAngelId() != 11l)
						&& (stub.getAngelId() != 2l) && (stub.getAngelId() != 3l) && (stub.getAngelId() != 4l)) {
						assertFalse(String.format("Unexpected invoiceline stub with willowid= %s and angelid= %s found in a queue", 
								stub.getWillowId(), stub.getAngelId()), true);
					}
			}  else if (stub instanceof VoucherStub) {
				if (stub.getAngelId().equals(2l)) {
					assertNotEquals("Voucher status should be active because success enrolment in the same invoice can not be canceled",
						ProductStatus.CANCELLED.getDatabaseValue(), ((VoucherStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Voucher with id= %s and status= %s found in a queue", stub.getWillowId(),
						((VoucherStub) stub).getStatus()), true);
				}
			} else if (stub instanceof ArticleStub) {
				if (stub.getAngelId().equals(3l)) {
					assertNotEquals("Article status should be active because success enrolment in the same invoice can not be canceled",
						ProductStatus.CANCELLED.getDatabaseValue(), ((ArticleStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Article with id= %s and status= %s found in a queue", stub.getWillowId(),
						((ArticleStub) stub).getStatus()), true);
				}
			} else if (stub instanceof MembershipStub) {
				if (stub.getAngelId().equals(1l)) {
					assertNotEquals("Membership status should be active because success enrolment in the same invoice can not be canceled",
						ProductStatus.CANCELLED.getDatabaseValue(), ((MembershipStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Membership with id= %s and status= %s found in a queue", stub.getWillowId(),
						((MembershipStub) stub).getStatus()), true);
				}
			}
		}
		logout();
	}
}
