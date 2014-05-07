package ish.oncourse.webservices.soap;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.util.GenericInvoiceStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.v6.stubs.replication.InvoiceStub;
import ish.oncourse.webservices.v6.stubs.replication.VoucherPaymentInStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public abstract class QEVoucherRedeemFailedNoGUITest extends QEVoucherRedeemNoGUITest {

	protected final BigDecimal getInvoiceTotalGst(GenericInvoiceStub stub) {
		return ((InvoiceStub) stub).getTotalGst();
	}

	protected final boolean isVoucherPaymentInStub(GenericReplicationStub stub) {
		return stub instanceof VoucherPaymentInStub;
	}

	protected final Integer getVoucherPaymentInStatus(GenericReplicationStub stub) {
		return ((VoucherPaymentInStub) stub).getStatus();
	}

	protected void checkAsyncReplicationForVoucherNoGUIFailed(ObjectContext context) {
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 15 records.", 15, queuedRecords.size());
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

		assertEquals("Not all PaymentIns found in a queue", 3, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 4, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 2, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 2, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 1, enrolmentsFound);
		assertEquals("Not all Vouchers found in a  queue", 1, vouchersFound);
		assertEquals("Not all Contacts found in a  queue", 1, contactsFound);
		assertEquals("Not all Students found in a  queue", 1, studentsFound);
	}
}
