package ish.oncourse.webservices.soap.v20;

import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.function.TestCase;
import ish.oncourse.webservices.function.TestEnvFunctions;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v17.stubs.replication.ArticleStub;
import ish.oncourse.webservices.v17.stubs.replication.MembershipStub;
import ish.oncourse.webservices.v17.stubs.replication.VoucherStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class QEPreviouslyKeepNonEnrolmentWithNewInvoiceTest extends QEPaymentProcess5_6CasesGUITest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEProcessCase6Dataset.xml";

	@Before
	public void before() throws Exception {
		testEnv = new TestEnv(DEFAULT_DATASET_XML, null);
		testEnv.start();
	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
				transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("22 elements should be replicated for this payment", 22, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
				if (stub.getWillowId() == 11l) {
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED_CARD_DECLINED, status);
				} else if (stub.getWillowId() == 12l) {
					PaymentType type = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getType(), PaymentType.class);
					assertEquals("Payment type should be CC", PaymentType.CREDIT_CARD, type);
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED, status);
				} else {
					assertFalse(String.format("Unexpected PaymentIn with id= %s and status= %s found in a queue", stub.getWillowId(),
							((GenericPaymentInStub) stub).getStatus()), true);
				}
			} else if (stub instanceof GenericEnrolmentStub) {
				assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(),
						((GenericEnrolmentStub) stub).getStatus()), true);
			} else if (stub instanceof VoucherStub) {
				if (stub.getAngelId().equals(2l) || stub.getAngelId().equals(10l)) {
					assertEquals("Voucher status should be active",
							ProductStatus.ACTIVE.getDatabaseValue(), ((VoucherStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Voucher with id= %s and status= %s found in a queue", stub.getWillowId(),
							((VoucherStub) stub).getStatus()), true);
				}
			} else if (stub instanceof ArticleStub) {
				if (stub.getAngelId().equals(3l)) {
					assertEquals("Article status should be active",
							ProductStatus.ACTIVE.getDatabaseValue(), ((ArticleStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Article with id= %s and status= %s found in a queue", stub.getWillowId(),
							((ArticleStub) stub).getStatus()), true);
				}
			} else if (stub instanceof MembershipStub) {
				if (stub.getAngelId().equals(1l)) {
					assertEquals("Membership status should be active",
							ProductStatus.ACTIVE.getDatabaseValue(), ((MembershipStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Membership with id= %s and status= %s found in a queue", stub.getWillowId(),
							((MembershipStub) stub).getStatus()), true);
				}
			}
		}
	}

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		//check that async replication works correct
		List<QueuedRecord> queuedRecords = ObjectSelect.query(QueuedRecord.class)
				.select(context);
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 21 records.", 21, queuedRecords.size());
		int paymentsFound = 0, paymentLinesFound = 0, invoicesFound = 0, invoiceLinesFound = 0,
				membershipsFound = 0, vouchersFound = 0, articlesFound = 0, contactsFound = 0, studentsFound = 0;
		for (QueuedRecord record : queuedRecords) {
			if (PAYMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				paymentsFound++;
			} else if (PAYMENT_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				paymentLinesFound++;
			} else if (INVOICE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				invoicesFound++;
			} else if (INVOICE_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				invoiceLinesFound++;
			} else if (MEMBERSHIP_IDENTIFIER.equals(record.getEntityIdentifier())) {
				membershipsFound++;
			} else if (VOUCHER_IDENTIFIER.equals(record.getEntityIdentifier())) {
				vouchersFound++;
			} else if (ARTICLE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				articlesFound++;
			} else if (CONTACT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				contactsFound++;
			} else if (STUDENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				studentsFound++;
			} else if (ENROLMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				assertFalse("No enrolment should exist in a queue", true);
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}

		assertEquals("Not all PaymentIns found in a queue", 2, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 4, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 3, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 8, invoiceLinesFound);
		assertEquals("Contact not found in a queue", 1, contactsFound);
		assertEquals("Student not found in a queue", 0, studentsFound);
		assertEquals("Membership not found in a queue", 1, membershipsFound);
		assertEquals("Voucher not found in a queue", 1, vouchersFound);
		assertEquals("Article not found in a queue", 1, articlesFound);
	}

	@Override
	protected String checkResponseAndReceiveSessionId(GenericTransactionGroup transaction) {
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
				assertFalse("No enrollments should be linked with this payment attempt", true);
			} else if (MEMBERSHIP_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Membership status should not change after this processing",
						ProductStatus.NEW.getDatabaseValue(), ((MembershipStub) stub).getStatus());
			} else if (VOUCHER_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				if (stub.getAngelId() == 2) {
					assertEquals("Voucher status should not change after this processing",
							ProductStatus.NEW.getDatabaseValue(), ((VoucherStub) stub).getStatus());
				} else if (stub.getAngelId() == 10) {
					assertEquals("Voucher status should not change after this processing",
							ProductStatus.ACTIVE.getDatabaseValue(), ((VoucherStub) stub).getStatus());
				}
			} else if (ARTICLE_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Article status should not change after this processing",
						ProductStatus.NEW.getDatabaseValue(), ((ArticleStub) stub).getStatus());
			}
		}
		return sessionId;
	}

	@Test
	public void testQEKeepInvoice() throws Exception {
		new TestCase(
				testEnv.getTestEnv(),
				this::fillv17PaymentStubs,
				this::checkResponseAndReceiveSessionId,
				this.testEnv.getTestEnv()::failedProcessing,
				this::checkAsyncReplication,
				this::checkProcessedResponse
		).test();
	}
}
