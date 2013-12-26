package ish.oncourse.webservices.soap;

import ish.common.types.*;
import ish.math.Money;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.replication.services.PortHelper;
import ish.oncourse.webservices.util.GenericInvoiceStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v6.stubs.replication.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

public abstract class CommonRealWSTransportTest extends RealWSTransportTest {

	protected final void checkQueueBeforeProcessing(ObjectContext context) {
		assertTrue("Queue should be empty before processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
	}

	protected final void checkQueueAfterProcessing(ObjectContext context) {
		assertTrue("Queue should be empty after processing", context.performQuery(new SelectQuery(QueuedRecord.class)).isEmpty());
	}

	protected abstract GenericTransactionGroup prepareStubsForReplication(GenericTransactionGroup transaction);

	protected final GenericTransactionGroup processPayment() throws Exception {
		authenticate();
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(getSupportedVersion());
		//process payment
		transaction = getPaymentPortType().processPayment(castGenericTransactionGroup(prepareStubsForReplication(transaction)));
		return transaction;
	}

	protected String checkResponseAndReceiveSessionId(GenericTransactionGroup transaction) {
		return null;
	}

	protected final GenericTransactionGroup getPaymentStatus(String sessionId) throws Exception {
		return getPaymentPortType().getPaymentStatus(sessionId);
	}

	protected final void checkNotProcessedResponse(GenericTransactionGroup transaction) {
		assertTrue("Get status call should return empty response for in transaction payment",
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
	}

	protected abstract void checkProcessedResponse(GenericTransactionGroup transaction);

	protected void checkAsyncReplication(ObjectContext context) {}

	protected final BigDecimal getInvoiceTotalGst(GenericInvoiceStub stub) {
		return ((InvoiceStub) stub).getTotalGst();
	}

	protected final boolean isVoucherPaymentInStub(GenericReplicationStub stub) {
		return stub instanceof VoucherPaymentInStub;
	}

	protected final Integer getVoucherPaymentInStatus(GenericReplicationStub stub) {
		return ((VoucherPaymentInStub) stub).getStatus();
	}

	protected final boolean isVoucherStub(GenericReplicationStub stub) {
		return stub instanceof VoucherStub;
	}

	protected final BigDecimal getVoucherRedemptionValue(GenericReplicationStub stub) {
		return ((VoucherStub) stub).getRedemptionValue();
	}

	protected final Integer getVoucherRedeemedCoursesCount(GenericReplicationStub stub) {
		return ((VoucherStub) stub).getRedeemedCoursesCount();
	}

	protected final Integer getVoucherProductStatus(GenericReplicationStub stub) {
		return ((VoucherStub) stub).getStatus();
	}

	protected Date getValidExpiryDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 2);
		return DateUtils.truncate(calendar, Calendar.DATE).getTime();
	}

	protected void testNoGUICases() throws Exception {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		checkQueueBeforeProcessing(context);

		GenericTransactionGroup transaction = processPayment();

		checkAsyncReplication(context);

		checkProcessedResponse(transaction);

		logout();
	}

	protected void testGUICases() throws Exception {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		checkQueueBeforeProcessing(context);

		GenericTransactionGroup transaction = processPayment();

		String sessionId = checkResponseAndReceiveSessionId(transaction);

		checkQueueAfterProcessing(context);

		transaction = getPaymentStatus(sessionId);

		checkNotProcessedResponse(transaction);

		//call page processing
		renderPaymentPageWithSuccessProcessing(sessionId);

		checkAsyncReplication(context);

		transaction = getPaymentStatus(sessionId);

		checkProcessedResponse(transaction);

		logout();
	}

	private GenericTransactionGroup preparePaymentStructureForTwoEnrolmentsWithoutVoucher(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Money hundredDollars = new Money("100.00");
		final Date current = new Date();

		PaymentInStub paymentInStub = new PaymentInStub();
		paymentInStub.setAngelId(1l);
		paymentInStub.setAmount(hundredDollars.toBigDecimal());
		paymentInStub.setContactId(1l);
		paymentInStub.setCreated(current);
		paymentInStub.setModified(current);
		paymentInStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		paymentInStub.setStatus(PaymentStatus.IN_TRANSACTION.getDatabaseValue());
		paymentInStub.setType(PaymentType.CREDIT_CARD.getDatabaseValue());
		paymentInStub.setEntityIdentifier(PAYMENT_IDENTIFIER);
		stubs.add(paymentInStub);

		PaymentInStub paymentInStub2 = new PaymentInStub();
		paymentInStub2.setAngelId(2l);
		paymentInStub2.setAmount(hundredDollars.toBigDecimal());
		paymentInStub2.setContactId(1l);
		paymentInStub2.setCreated(current);
		paymentInStub2.setModified(current);
		paymentInStub2.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		paymentInStub2.setStatus(PaymentStatus.IN_TRANSACTION.getDatabaseValue());
		paymentInStub2.setType(PaymentType.VOUCHER.getDatabaseValue());
		paymentInStub2.setEntityIdentifier(PAYMENT_IDENTIFIER);
		stubs.add(paymentInStub2);

		InvoiceStub invoiceStub = new InvoiceStub();
		invoiceStub.setContactId(1l);
		invoiceStub.setAmountOwing(hundredDollars.multiply(2l).toBigDecimal());
		invoiceStub.setAngelId(10l);
		invoiceStub.setCreated(current);
		invoiceStub.setDateDue(current);
		invoiceStub.setEntityIdentifier(INVOICE_IDENTIFIER);
		invoiceStub.setInvoiceDate(current);
		invoiceStub.setInvoiceNumber(322l);
		invoiceStub.setModified(current);
		invoiceStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		invoiceStub.setTotalExGst(invoiceStub.getAmountOwing());
		invoiceStub.setTotalGst(invoiceStub.getAmountOwing());
		stubs.add(invoiceStub);

		PaymentInLineStub paymentLineStub = new PaymentInLineStub();
		paymentLineStub.setAngelId(1l);
		paymentLineStub.setAmount(paymentInStub.getAmount());
		paymentLineStub.setCreated(current);
		paymentLineStub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub.setInvoiceId(invoiceStub.getAngelId());//link with original invoice
		paymentLineStub.setModified(current);
		paymentLineStub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub);

		PaymentInLineStub paymentLineStub2 = new PaymentInLineStub();
		paymentLineStub2.setAngelId(2l);
		paymentLineStub2.setAmount(paymentInStub2.getAmount());
		paymentLineStub2.setCreated(current);
		paymentLineStub2.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub2.setInvoiceId(invoiceStub.getAngelId());//link with original invoice
		paymentLineStub2.setModified(current);
		paymentLineStub2.setPaymentInId(paymentInStub2.getAngelId());
		stubs.add(paymentLineStub2);

		InvoiceLineStub invoiceLineStub = new InvoiceLineStub();
		invoiceLineStub.setAngelId(1l);
		invoiceLineStub.setCreated(current);
		invoiceLineStub.setDescription(StringUtils.EMPTY);
		invoiceLineStub.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub.setModified(current);
		invoiceLineStub.setPriceEachExTax(hundredDollars.toBigDecimal());
		invoiceLineStub.setQuantity(BigDecimal.ONE);
		invoiceLineStub.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub);

		EnrolmentStub enrolmentStub = new EnrolmentStub();
		enrolmentStub.setAngelId(1l);
		enrolmentStub.setCourseClassId(1l);
		enrolmentStub.setCreated(current);
		enrolmentStub.setEntityIdentifier(ENROLMENT_IDENTIFIER);
		enrolmentStub.setInvoiceLineId(invoiceLineStub.getAngelId());
		enrolmentStub.setModified(current);
		enrolmentStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		enrolmentStub.setStatus(EnrolmentStatus.IN_TRANSACTION.name());
		enrolmentStub.setStudentId(1l);
		//link the invoiceLine with enrolment
		invoiceLineStub.setEnrolmentId(enrolmentStub.getAngelId());
		stubs.add(enrolmentStub);

		InvoiceLineStub invoiceLineStub2 = new InvoiceLineStub();
		invoiceLineStub2.setAngelId(2l);
		invoiceLineStub2.setCreated(current);
		invoiceLineStub2.setDescription(StringUtils.EMPTY);
		invoiceLineStub2.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub2.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub2.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub2.setModified(current);
		invoiceLineStub2.setPriceEachExTax(hundredDollars.toBigDecimal());
		invoiceLineStub2.setQuantity(BigDecimal.ONE);
		invoiceLineStub2.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub2.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub2);

		EnrolmentStub enrolmentStub2 = new EnrolmentStub();
		enrolmentStub2.setAngelId(2l);
		enrolmentStub2.setCourseClassId(1l);
		enrolmentStub2.setCreated(current);
		enrolmentStub2.setEntityIdentifier(ENROLMENT_IDENTIFIER);
		enrolmentStub2.setInvoiceLineId(invoiceLineStub2.getAngelId());
		enrolmentStub2.setModified(current);
		enrolmentStub2.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		enrolmentStub2.setStatus(EnrolmentStatus.IN_TRANSACTION.name());
		enrolmentStub2.setStudentId(2l);
		//link the invoiceLine with enrolment
		invoiceLineStub2.setEnrolmentId(enrolmentStub2.getAngelId());
		stubs.add(enrolmentStub2);

		return transaction;
	}

	protected GenericTransactionGroup preparePaymentStructureForMoneyVoucherAndMoneyPayment(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = preparePaymentStructureForTwoEnrolmentsWithoutVoucher(transaction).getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Date current = new Date();

		VoucherPaymentInStub voucherPaymentInStub = new VoucherPaymentInStub();
		voucherPaymentInStub.setAngelId(1l);
		voucherPaymentInStub.setCreated(current);
		voucherPaymentInStub.setModified(current);
		voucherPaymentInStub.setEntityIdentifier(VOUCHER_PAYMENT_IN_IDENTIFIER);
		voucherPaymentInStub.setEnrolmentsCount(1);
		voucherPaymentInStub.setPaymentInId(2l);
		voucherPaymentInStub.setVoucherId(4l);
		voucherPaymentInStub.setStatus(VoucherPaymentStatus.APPROVED.getDatabaseValue());
		stubs.add(voucherPaymentInStub);

		return transaction;
	}

	protected GenericTransactionGroup preparePaymentStructureForCourseVoucherAndMoneyPayment(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = preparePaymentStructureForTwoEnrolmentsWithoutVoucher(transaction).getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Date current = new Date();

		VoucherPaymentInStub voucherPaymentInStub = new VoucherPaymentInStub();
		voucherPaymentInStub.setAngelId(1l);
		voucherPaymentInStub.setCreated(current);
		voucherPaymentInStub.setModified(current);
		voucherPaymentInStub.setEntityIdentifier(VOUCHER_PAYMENT_IN_IDENTIFIER);
		voucherPaymentInStub.setInvoiceLineId(1l);
		voucherPaymentInStub.setEnrolmentsCount(1);
		voucherPaymentInStub.setPaymentInId(2l);
		voucherPaymentInStub.setVoucherId(4l);
		voucherPaymentInStub.setStatus(VoucherPaymentStatus.APPROVED.getDatabaseValue());
		stubs.add(voucherPaymentInStub);

		return transaction;
	}

	private GenericTransactionGroup preparePaymentStructureWithoutVoucher(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Money hundredDollars = new Money("100.00");
		final Date current = new Date();

		PaymentInStub paymentInStub = new PaymentInStub();
		paymentInStub.setAngelId(1l);
		paymentInStub.setAmount(BigDecimal.ZERO);
		paymentInStub.setContactId(1l);
		paymentInStub.setCreated(current);
		paymentInStub.setModified(current);
		paymentInStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		paymentInStub.setStatus(PaymentStatus.IN_TRANSACTION.getDatabaseValue());
		paymentInStub.setType(PaymentType.INTERNAL.getDatabaseValue());
		paymentInStub.setEntityIdentifier(PAYMENT_IDENTIFIER);
		stubs.add(paymentInStub);

		PaymentInStub paymentInStub2 = new PaymentInStub();
		paymentInStub2.setAngelId(2l);
		paymentInStub2.setAmount(hundredDollars.toBigDecimal());
		paymentInStub2.setContactId(1l);
		paymentInStub2.setCreated(current);
		paymentInStub2.setModified(current);
		paymentInStub2.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		paymentInStub2.setStatus(PaymentStatus.IN_TRANSACTION.getDatabaseValue());
		paymentInStub2.setType(PaymentType.VOUCHER.getDatabaseValue());
		paymentInStub2.setEntityIdentifier(PAYMENT_IDENTIFIER);
		stubs.add(paymentInStub2);

		InvoiceStub invoiceStub = new InvoiceStub();
		invoiceStub.setContactId(1l);
		invoiceStub.setAmountOwing(hundredDollars.toBigDecimal());
		invoiceStub.setAngelId(10l);
		invoiceStub.setCreated(current);
		invoiceStub.setDateDue(current);
		invoiceStub.setEntityIdentifier(INVOICE_IDENTIFIER);
		invoiceStub.setInvoiceDate(current);
		invoiceStub.setInvoiceNumber(322l);
		invoiceStub.setModified(current);
		invoiceStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		invoiceStub.setTotalExGst(invoiceStub.getAmountOwing());
		invoiceStub.setTotalGst(invoiceStub.getAmountOwing());
		stubs.add(invoiceStub);

		PaymentInLineStub paymentLineStub = new PaymentInLineStub();
		paymentLineStub.setAngelId(1l);
		paymentLineStub.setAmount(paymentInStub.getAmount());
		paymentLineStub.setCreated(current);
		paymentLineStub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub.setInvoiceId(invoiceStub.getAngelId());//link with original invoice
		paymentLineStub.setModified(current);
		paymentLineStub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub);

		PaymentInLineStub paymentLineStub2 = new PaymentInLineStub();
		paymentLineStub2.setAngelId(2l);
		paymentLineStub2.setAmount(paymentInStub2.getAmount());
		paymentLineStub2.setCreated(current);
		paymentLineStub2.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub2.setInvoiceId(invoiceStub.getAngelId());//link with original invoice
		paymentLineStub2.setModified(current);
		paymentLineStub2.setPaymentInId(paymentInStub2.getAngelId());
		stubs.add(paymentLineStub2);

		InvoiceLineStub invoiceLineStub = new InvoiceLineStub();
		invoiceLineStub.setAngelId(1l);
		invoiceLineStub.setCreated(current);
		invoiceLineStub.setDescription(StringUtils.EMPTY);
		invoiceLineStub.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub.setModified(current);
		invoiceLineStub.setPriceEachExTax(hundredDollars.toBigDecimal());
		invoiceLineStub.setQuantity(BigDecimal.ONE);
		invoiceLineStub.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub);

		EnrolmentStub enrolmentStub = new EnrolmentStub();
		enrolmentStub.setAngelId(1l);
		enrolmentStub.setCourseClassId(1l);
		enrolmentStub.setCreated(current);
		enrolmentStub.setEntityIdentifier(ENROLMENT_IDENTIFIER);
		enrolmentStub.setInvoiceLineId(invoiceLineStub.getAngelId());
		enrolmentStub.setModified(current);
		enrolmentStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		enrolmentStub.setStatus(EnrolmentStatus.IN_TRANSACTION.name());
		enrolmentStub.setStudentId(1l);
		//link the invoiceLine with enrolment
		invoiceLineStub.setEnrolmentId(enrolmentStub.getAngelId());
		stubs.add(enrolmentStub);

		return transaction;
	}

	protected GenericTransactionGroup prepareMoneyVoucherNoMoneyPayment(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = preparePaymentStructureWithoutVoucher(transaction).getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Date current = new Date();

		VoucherPaymentInStub voucherPaymentInStub = new VoucherPaymentInStub();
		voucherPaymentInStub.setAngelId(1l);
		voucherPaymentInStub.setCreated(current);
		voucherPaymentInStub.setModified(current);
		voucherPaymentInStub.setEntityIdentifier(VOUCHER_PAYMENT_IN_IDENTIFIER);
		voucherPaymentInStub.setEnrolmentsCount(1);
		voucherPaymentInStub.setPaymentInId(2l);
		voucherPaymentInStub.setVoucherId(4l);
		voucherPaymentInStub.setStatus(VoucherPaymentStatus.APPROVED.getDatabaseValue());
		stubs.add(voucherPaymentInStub);

		return transaction;
	}

	protected GenericTransactionGroup prepareCourseVoucherNoMoneyPayment(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = preparePaymentStructureWithoutVoucher(transaction).getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Date current = new Date();

		VoucherPaymentInStub voucherPaymentInStub = new VoucherPaymentInStub();
		voucherPaymentInStub.setAngelId(1l);
		voucherPaymentInStub.setCreated(current);
		voucherPaymentInStub.setModified(current);
		voucherPaymentInStub.setEntityIdentifier(VOUCHER_PAYMENT_IN_IDENTIFIER);
		voucherPaymentInStub.setInvoiceLineId(1l);
		voucherPaymentInStub.setEnrolmentsCount(1);
		voucherPaymentInStub.setPaymentInId(2l);
		voucherPaymentInStub.setVoucherId(4l);
		voucherPaymentInStub.setStatus(VoucherPaymentStatus.APPROVED.getDatabaseValue());
		stubs.add(voucherPaymentInStub);

		return transaction;
	}
}
