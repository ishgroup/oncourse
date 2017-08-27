package ish.oncourse.webservices.soap.v16;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.ProductStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.util.*;
import ish.oncourse.webservices.v16.stubs.replication.ArticleStub;
import ish.oncourse.webservices.v16.stubs.replication.MembershipStub;
import ish.oncourse.webservices.v16.stubs.replication.VoucherStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class QEFailedPaymentForPartiallyCanceledTest extends QEPaymentProcess8CaseGUITest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEProcessCase8Dataset.xml";

	@Before
	public void before() throws Exception {
		testEnv = new V16TestEnv(DEFAULT_DATASET_XML, null);
		testEnv.start();
	}

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		//check that async replication works correct
		List<QueuedRecord> queuedRecords = ObjectSelect.query(QueuedRecord.class)
				.select(context);
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 25 records.", 25, queuedRecords.size());
		int paymentsFound = 0, paymentLinesFound = 0, invoicesFound = 0, invoiceLinesFound = 0,
				enrolmentsFound = 0, membershipsFound = 0, vouchersFound = 0, articlesFound = 0, contactFound = 0;
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
			} else if (MEMBERSHIP_IDENTIFIER.equals(record.getEntityIdentifier())) {
				membershipsFound++;
			} else if (VOUCHER_IDENTIFIER.equals(record.getEntityIdentifier())) {
				vouchersFound++;
			} else if (ARTICLE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				articlesFound++;
			} else if (CONTACT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				contactFound++;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}
		assertEquals("Not all PaymentIns found in a queue", 2, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 2, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 3, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 10, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 4, enrolmentsFound);
		assertEquals("Membership not found in a queue", 1, membershipsFound);
		assertEquals("Voucher not found in a queue", 1, vouchersFound);
		assertEquals("Article not found in a queue", 1, articlesFound);
		assertEquals("Contact not found in a queue", 1, contactFound);

	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
				transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("24 elements should be replicated for this payment", 24, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
				if (stub.getAngelId() == null) {
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED, status);
				} else if (stub.getAngelId() == 1l) {
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
							((GenericEnrolmentStub) stub).getStatus()), true);
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
			} else if (stub instanceof VoucherStub) {
				if (stub.getAngelId().equals(2l)) {
					assertEquals("Voucher status should be active because success enrolment in the same invoice can not be canceled",
							ProductStatus.ACTIVE.getDatabaseValue(), ((VoucherStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Voucher with id= %s and status= %s found in a queue", stub.getWillowId(),
							((VoucherStub) stub).getStatus()), true);
				}
			} else if (stub instanceof ArticleStub) {
				if (stub.getAngelId().equals(3l)) {
					assertEquals("Article status should be active because success enrolment in the same invoice can not be canceled",
							ProductStatus.ACTIVE.getDatabaseValue(), ((ArticleStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Article with id= %s and status= %s found in a queue", stub.getWillowId(),
							((ArticleStub) stub).getStatus()), true);
				}
			} else if (stub instanceof MembershipStub) {
				if (stub.getAngelId().equals(1l)) {
					assertEquals("Membership status should be active because success enrolment in the same invoice can not be canceled",
							ProductStatus.ACTIVE.getDatabaseValue(), ((MembershipStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Membership with id= %s and status= %s found in a queue", stub.getWillowId(),
							((MembershipStub) stub).getStatus()), true);
				}
			}
		}
	}

	@Override
	protected String checkResponseAndReceiveSessionId(GenericTransactionGroup transaction) {
		String sessionId = null;
		assertEquals("22 stubs should be in response for this processing", 22, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
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
						ProductStatus.NEW.getDatabaseValue(), ((MembershipStub) stub).getStatus());
			} else if (VOUCHER_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Voucher status should not change after this processing",
						ProductStatus.NEW.getDatabaseValue(), ((VoucherStub) stub).getStatus());
			} else if (ARTICLE_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Article status should not change after this processing",
						ProductStatus.NEW.getDatabaseValue(), ((ArticleStub) stub).getStatus());
			}
		}
		return sessionId;
	}


	@Test
	public void testQEKeepInvoice() throws Exception {
		new V16TestEnv.TestCase(
				testEnv,
				this::fillv16PaymentStubsForCases8,
				this::checkResponseAndReceiveSessionId,
				this.testEnv::renderPaymentPageWithKeepInvoiceProcessing,
				this::checkAsyncReplication,
				this::checkProcessedResponse).test();
	}
}
