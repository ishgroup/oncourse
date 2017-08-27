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
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class QEFailedPaymentKeepInvoiceTest extends QEPaymentProcess1_4CasesGUITest {

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		List<QueuedRecord> queuedRecords = ObjectSelect.query(QueuedRecord.class)
				.select(context);
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 20 records.", 20, queuedRecords.size());
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
		assertEquals("Not all Invoices found in a queue", 2, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 8, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 2, enrolmentsFound);
		assertEquals("Membership not found in a queue", 1, membershipsFound);
		assertEquals("Voucher not found in a queue", 1, vouchersFound);
		assertEquals("Article not found in a queue", 1, articlesFound);
		assertEquals("Contact not found in a queue", 1, contactFound);
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
				if (stub.getAngelId() != null) {
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED_CARD_DECLINED, status);
				} else {
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED, status);
				}
			} else if (stub instanceof GenericEnrolmentStub) {
				if (stub.getAngelId() == 1l) {
					EnrolmentStatus status = EnrolmentStatus.valueOf(((GenericEnrolmentStub) stub).getStatus());
					assertEquals("Oncourse enrollment should be success", EnrolmentStatus.SUCCESS, status);
				} else {
					assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(),
							((GenericEnrolmentStub) stub).getStatus()), true);
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
		assertEquals("20 stubs should be in response for this processing", 20, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
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
		//check that empty queuedRecords
		ObjectContext context = testEnv.getCayenneService().newNonReplicatingContext();
		testEnv.checkQueueBeforeProcessing(context);
		testEnv.authenticate();
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(testEnv.getSupportedVersion());
		GenericParametersMap parametersMap = PortHelper.createParametersMap(testEnv.getSupportedVersion());
		fillv16PaymentStubs(transaction, parametersMap);
		//process payment
		transaction = testEnv.getPaymentPortType().processPayment(testEnv.castGenericTransactionGroup(transaction),
				testEnv.castGenericParametersMap(parametersMap));
		//check the response, validate the data and receive the sessionid
		String sessionId = checkResponseAndReceiveSessionId(transaction);
		testEnv.checkQueueAfterProcessing(context);
		//check the status via service
		testEnv.checkNotProcessedResponse(testEnv.getPaymentStatus(sessionId));
		//call page processing
		testEnv.renderPaymentPageWithKeepInvoiceProcessing(sessionId);
		//check that async replication works correct
		checkAsyncReplication(context);
		//check the status via service when processing complete
		checkProcessedResponse(testEnv.getPaymentStatus(sessionId));
	}

}
