package ish.oncourse.webservices.soap.v16;

import ish.common.types.*;
import ish.math.Money;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.replication.services.ReplicationUtils;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.v16.stubs.replication.*;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public abstract class QEVoucherRedeemNoGUITest extends QEPaymentProcessTest {

	protected void testNoGUICases() throws Exception {
		ObjectContext context = testEnv.getTestEnv().getCayenneService().newNonReplicatingContext();

		testEnv.getTestEnv().checkQueueBeforeProcessing(context);

		GenericTransactionGroup transaction = processPayment();

		checkAsyncReplication(context);

		checkProcessedResponse(transaction);
	}

	protected final void preparePaymentStructureWithoutVoucher(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
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

		parametersMap.getGenericEntry().add(createEntry(String.format("%s_%d", PaymentIn.class.getSimpleName(), paymentInStub.getAngelId()),
				paymentInStub.getAngelId().toString()));

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

		parametersMap.getGenericEntry().add(createEntry(String.format("%s_%d", Invoice.class.getSimpleName(),invoiceStub.getAngelId()),
				invoiceStub.getAngelId().toString()));

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

		parametersMap.getGenericEntry().add(createEntry(String.format("%s_%d", Enrolment.class.getSimpleName(), enrolmentStub.getAngelId()),
				enrolmentStub.getAngelId().toString()));
	}

	protected abstract void prepareStubsForReplication(GenericTransactionGroup transaction, GenericParametersMap parametersMap);

	protected final GenericTransactionGroup processPayment() throws Exception {
		testEnv.getTestEnv().authenticate();
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(testEnv.getTestEnv().getSupportedVersion());
		GenericParametersMap parametersMap = PortHelper.createParametersMap(testEnv.getTestEnv().getSupportedVersion());
		
		prepareStubsForReplication(transaction, parametersMap);
		
		//process payment
		transaction = testEnv.getTestEnv().processPayment(transaction, parametersMap);
		return transaction;
	}

	protected void prepareCourseVoucherNoMoneyPayment(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
		preparePaymentStructureWithoutVoucher(transaction, parametersMap);
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
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

	}

	protected void prepareMoneyVoucherNoMoneyPayment(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
		preparePaymentStructureWithoutVoucher(transaction, parametersMap);
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
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

	}
}
