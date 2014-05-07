package ish.oncourse.webservices.soap;

import ish.common.types.*;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v6.stubs.replication.VoucherStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public abstract class QEVoucherRedeemSuccessNoGUITest extends QEVoucherRedeemNoGUITest {

	protected final void checkProcessedResponseForVoucherNoGUI(GenericTransactionGroup transaction) {
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
			} else if (stub instanceof VoucherStub) {
				switch (((VoucherStub) stub).getRedeemedCoursesCount()) {
					case 0 :
						assertEquals("Check of voucher redemption value failed", ((VoucherStub) stub).getRedemptionValue(), new BigDecimal("100.00"));
						break;
					case 1 :
						assertEquals("Check of voucher redemption value failed", ((VoucherStub) stub).getRedemptionValue(), new BigDecimal("10.00"));
						break;
					default:
						assertFalse("Unexpected voucher redeemed course count", true);
				}
				assertEquals("Check of voucher status failed", ((VoucherStub) stub).getStatus(), ProductStatus.ACTIVE.getDatabaseValue());
			}
		}
	}

	protected final void checkAsyncReplicationForVoucherNoGUI(ObjectContext context) {
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 9 records.", 9, queuedRecords.size());
		int paymentsFound = 0, paymentLinesFound = 0, invoicesFound = 0, invoiceLinesFound = 0, enrolmentsFound = 0,
                voucherPaymentInsFound = 0, vouchersFound=0;

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
			} else if (VOUCHER_IDENTIFIER.equals(record.getEntityIdentifier())) {
                vouchersFound++;
            }  else if (VOUCHER_PAYMENT_IN_IDENTIFIER.equals(record.getEntityIdentifier())) {
                voucherPaymentInsFound++;
            } else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}

		assertEquals("Not all PaymentIns found in a queue", 2, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 2, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 1, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 1, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 1, enrolmentsFound);
        assertEquals("Not all Vouchers found in a  queue", 1, vouchersFound);
        assertEquals("Not all VoucherPaymentIn found in a queue", 1, voucherPaymentInsFound);
	}
}
