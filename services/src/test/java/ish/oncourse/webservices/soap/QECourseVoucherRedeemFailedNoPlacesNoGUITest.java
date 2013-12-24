package ish.oncourse.webservices.soap;

import ish.common.types.*;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class QECourseVoucherRedeemFailedNoPlacesNoGUITest extends CommonRealWSTransportTest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QECourseVoucherRedeemFailedNoPlacesDataSet.xml";
	private static TestServer server;

	@Override
	protected TestServer getServer() {
		return server;
	}

	@BeforeClass
	public static void initTestServer() throws Exception {
		server = startRealWSServer(9103);
	}

	@Override
	protected String getDataSetFile() {
		return DEFAULT_DATASET_XML;
	}

	@Override
	protected GenericTransactionGroup prepareStubsForReplication(GenericTransactionGroup transaction) {
		return prepareCourseVoucherNoMoneyPayment(transaction);
	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("23 elements should be replicated for this payment", 23, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				GenericPaymentInStub paymentInStub = (GenericPaymentInStub) stub;
				if (stub.getAngelId() == null) {
					if (stub.getWillowId() == 3l || stub.getWillowId() == 4l) {
						assertEquals("This should be 0 amount internal payment", PaymentType.REVERSE.getDatabaseValue(), paymentInStub.getType());
						PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
						assertEquals("Payment status should be success", PaymentStatus.SUCCESS, status);
					} else {
						assertFalse(String.format("Unexpected PaymentIn with id= %s angelid=%s and status= %s found in a queue",
							stub.getWillowId(), stub.getAngelId(), paymentInStub.getStatus()), true);
					}
				} else if (stub.getAngelId() == 1l) {
					assertEquals("This should be 0 amount internal payment", PaymentType.INTERNAL.getDatabaseValue(), paymentInStub.getType());
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be failed no places", PaymentStatus.FAILED_NO_PLACES, status);
				} else if (stub.getAngelId() == 2l) {
					assertEquals("This should be voucher payment", PaymentType.VOUCHER.getDatabaseValue(), paymentInStub.getType());
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED, status);
				} else {
					assertFalse(String.format("Unexpected PaymentIn with id= %s angelid=%s and status= %s found in a queue",
							stub.getWillowId(), stub.getAngelId(), paymentInStub.getStatus()), true);
				}
			} else if (stub instanceof GenericEnrolmentStub) {
				if (stub.getAngelId() == 1l) {
					EnrolmentStatus status = EnrolmentStatus.valueOf(((GenericEnrolmentStub) stub).getStatus());
					assertEquals("Oncourse enrollment should be success after expiration", EnrolmentStatus.FAILED, status);
				} else {
					assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(),
							((GenericEnrolmentStub)stub).getStatus()), true);
				}
			} else if (stub instanceof GenericInvoiceStub) {
				GenericInvoiceStub invoiceStub = (GenericInvoiceStub) stub;
				if (stub.getAngelId() == null && stub.getWillowId() == 2l) {
					assertEquals("Incorrect reverse invoice amount", getInvoiceTotalGst(invoiceStub), new BigDecimal("-100.00"));
				} else if (stub.getAngelId() == 10l) {
					assertEquals("Incorrect original invoice amount", getInvoiceTotalGst(invoiceStub), new BigDecimal("100.00"));
				} else {
					assertFalse(String.format("Unexpected Invoice with id= %s and angelid= %s found in a queue",
						stub.getWillowId(), stub.getAngelId()), true);
				}
			} else if (isVoucherPaymentInStub(stub)) {
				assertEquals("Incorrect VoucherPaymentIn status", getVoucherPaymentInStatus(stub), VoucherPaymentStatus.APPROVED.getDatabaseValue());
			} else if (isVoucherStub(stub)) {
				assertEquals("Check of voucher redemption value failed", getVoucherRedemptionValue(stub), new BigDecimal("10.00"));
				assertEquals("Check of voucher status failed", getVoucherProductStatus(stub), ProductStatus.ACTIVE.getDatabaseValue());
				assertEquals("Check of voucher redeemed courses count failed", getVoucherRedeemedCoursesCount(stub), Integer.valueOf(0));
			}
		}
	}

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 18 records.", 18, queuedRecords.size());
		int paymentsFound = 0, paymentLinesFound = 0, invoicesFound = 0, invoiceLinesFound = 0, enrolmentsFound = 0,
		vouchersFound = 0, contactsFound = 0, studentsFound = 0;

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
			} else if (VOUCHER_IDENTIFIER.equals(record.getEntityIdentifier())){
				vouchersFound++;
			} else if (CONTACT_IDENTIFIER.equals(record.getEntityIdentifier())){
				contactsFound++;
			} else if (STUDENT_IDENTIFIER.equals(record.getEntityIdentifier())){
				studentsFound++;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}

		assertEquals("Not all PaymentIns found in a queue", 4, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 6, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 2, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 2, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 1, enrolmentsFound);
		assertEquals("Not all Vouchers found in a  queue", 1, vouchersFound);
		assertEquals("Not all Contacts found in a  queue", 1, contactsFound);
		assertEquals("Not all Students found in a  queue", 1, studentsFound);
	}

	@Test
	public void testQEFailedPayment() throws Exception {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		checkQueueBeforeProcessing(context);

		GenericTransactionGroup transaction = processPayment();

		checkAsyncReplication(context);

		checkProcessedResponse(transaction);

		logout();
	}
}
