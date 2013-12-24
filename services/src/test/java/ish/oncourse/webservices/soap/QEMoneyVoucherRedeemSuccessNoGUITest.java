package ish.oncourse.webservices.soap;

import ish.common.types.*;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class QEMoneyVoucherRedeemSuccessNoGUITest extends CommonRealWSTransportTest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEMoneyVoucherRedeemSuccessDataSet.xml";
	private static TestServer server;

	@Override
	protected TestServer getServer() {
		return server;
	}

	@BeforeClass
	public static void initTestServer() throws Exception {
		server = startRealWSServer(9104);
	}

	@Override
	protected String getDataSetFile() {
		return DEFAULT_DATASET_XML;
	}

	@Override
	protected GenericTransactionGroup prepareStubsForReplication(GenericTransactionGroup transaction) {
		return prepareMoneyVoucherNoMoneyPayment(transaction);
	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("15 elements should be replicated for this payment", 15, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				GenericPaymentInStub paymentInStub = (GenericPaymentInStub) stub;
				if (stub.getAngelId() == 1l) {
					assertEquals("This should be 0 amount internal payment", PaymentType.INTERNAL.getDatabaseValue(), paymentInStub.getType());
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be success", PaymentStatus.SUCCESS, status);
				} else if (stub.getAngelId() == 2l) {
					assertEquals("This should be voucher payment", PaymentType.VOUCHER.getDatabaseValue(), paymentInStub.getType());
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be success", PaymentStatus.SUCCESS, status);
				} else {
					assertFalse(String.format("Unexpected PaymentIn with id= %s angelid=%s and status= %s found in a queue",
						stub.getWillowId(), stub.getAngelId(), paymentInStub.getStatus()), true);
				}
			} else if (stub instanceof GenericEnrolmentStub) {
				if (stub.getAngelId() == 1l) {
					EnrolmentStatus status = EnrolmentStatus.valueOf(((GenericEnrolmentStub) stub).getStatus());
					assertEquals("Oncourse enrollment should be success", EnrolmentStatus.SUCCESS, status);
				} else {
					assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(),
							((GenericEnrolmentStub)stub).getStatus()), true);
				}
			} else if (isVoucherStub(stub)) {
				assertEquals("Check of voucher redemption value failed", getVoucherRedemptionValue(stub), new BigDecimal("100.00"));
				assertEquals("Check of voucher status failed", getVoucherProductStatus(stub), ProductStatus.ACTIVE.getDatabaseValue());
				assertEquals("Check of voucher redeemed courses count failed", getVoucherRedeemedCoursesCount(stub), Integer.valueOf(0));
			}
		}
	}

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 7 records.", 7, queuedRecords.size());
		int paymentsFound = 0, paymentLinesFound = 0, invoicesFound = 0, invoiceLinesFound = 0, enrolmentsFound = 0;

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
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}

		assertEquals("Not all PaymentIns found in a queue", 2, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 2, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 1, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 1, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 1, enrolmentsFound);
	}

	@Test
	public void testQESuccessPayment() throws Exception {
		testNoGUICases();
	}
}
