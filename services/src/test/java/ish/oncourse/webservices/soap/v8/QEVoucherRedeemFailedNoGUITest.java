package ish.oncourse.webservices.soap.v8;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.util.GenericInvoiceStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.v8.stubs.replication.InvoiceStub;
import ish.oncourse.webservices.v8.stubs.replication.VoucherPaymentInStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
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
		assertEquals("Queue should contain 16 records.", 16, queuedRecords.size());

		Expression exp = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, PAYMENT_IDENTIFIER);
		assertEquals("Not all PaymentIns found in a queue", 3, exp.filterObjects(queuedRecords).size());
		exp = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, PAYMENT_LINE_IDENTIFIER);
		assertEquals("Not all PaymentInLines found in a queue", 4, exp.filterObjects(queuedRecords).size());
		exp = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, INVOICE_IDENTIFIER);
		assertEquals("Not all Invoices found in a queue", 2, exp.filterObjects(queuedRecords).size());
		exp = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, INVOICE_LINE_IDENTIFIER);
		assertEquals("Not all InvoiceLines found in a  queue", 2, exp.filterObjects(queuedRecords).size());
		exp = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, ENROLMENT_IDENTIFIER);
		assertEquals("Not all Enrolments found in a  queue", 1, exp.filterObjects(queuedRecords).size());
		exp = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, VOUCHER_PAYMENT_IN_IDENTIFIER);
		assertEquals("Not all VoucherPaymentIns found in a  queue", 1, exp.filterObjects(queuedRecords).size());
		exp = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, VOUCHER_IDENTIFIER);
		assertEquals("Not all Vouchers found in a  queue", 1, exp.filterObjects(queuedRecords).size());
		exp = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, CONTACT_IDENTIFIER);
		assertEquals("Not all Contacts found in a  queue", 1, exp.filterObjects(queuedRecords).size());
		exp = ExpressionFactory.matchExp(QueuedRecord.ENTITY_IDENTIFIER_PROPERTY, STUDENT_IDENTIFIER);
		assertEquals("Not all Students found in a  queue", 1, exp.filterObjects(queuedRecords).size());
	}
}
