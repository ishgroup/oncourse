package ish.oncourse.webservices.soap;

import ish.common.types.*;
import ish.math.Money;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v6.stubs.replication.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public abstract class QEVoucherRedeemWithMoneyPaymentGUITest extends QEVoucherRedeemNoGUITest {

	protected final void checkProcessedResponseForVoucherAndCreditCardPayment(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
				transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("19 elements should be replicated for this payment", 19, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				GenericPaymentInStub paymentInStub = (GenericPaymentInStub) stub;
				if (stub.getAngelId() == 1l) {
					assertEquals("This should be 0 amount internal payment", PaymentType.CREDIT_CARD.getDatabaseValue(), paymentInStub.getType());
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
				if (stub.getAngelId() == 1l || stub.getAngelId() == 2l) {
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

	protected final String checkResponseAndReceiveSessionIdForVoucherAndCreditCardPayment(GenericTransactionGroup transaction) {
		String sessionId = null;
		assertEquals("19 stubs should be in response for this processing", 19, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			assertNotNull("Willowid after the first payment processing should not be NULL", stub.getWillowId());
			if (PAYMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				GenericPaymentInStub payment = (GenericPaymentInStub) stub;
				assertEquals("Payment status should not change after this processing", PaymentStatus.IN_TRANSACTION.getDatabaseValue(), payment.getStatus());
				if (PaymentType.CREDIT_CARD.getDatabaseValue().equals(payment.getType())) {
					assertNull("Only 1 credit card paymentIn entity should exist in response. This entity sessionid will be used for page processing", sessionId);
					sessionId = payment.getSessionId();
					assertNotNull("PaymentIn entity should contain the sessionid after processing", sessionId);
				} else if (PaymentType.VOUCHER.getDatabaseValue().equals(payment.getType())) {
					assertNull("Voucher payment should not contain sessionid", payment.getSessionId());
				} else {
					assertFalse(String.format("Unexpected payment in response with angelid=%s and willowid=%s", stub.getAngelId(), stub.getWillowId()), true);
				}
			} else if (ENROLMENT_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Enrolment status should not change after this processing", EnrolmentStatus.IN_TRANSACTION.name(),
						((GenericEnrolmentStub) stub).getStatus());
			} else if (VOUCHER_IDENTIFIER.equals(stub.getEntityIdentifier())) {
				assertEquals("Voucher status should not change after this processing",
						ProductStatus.ACTIVE.getDatabaseValue(), ((ish.oncourse.webservices.v6.stubs.replication.VoucherStub)stub).getStatus());
			}
		}
		return sessionId;
	}

	protected final void checkAsyncReplicationForVoucherAndCreditCardPayment(ObjectContext context) {
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 9 records.", 9, queuedRecords.size());
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
		assertEquals("Not all InvoiceLines found in a  queue", 2, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 2, enrolmentsFound);
	}

	protected final void checkAsyncReplicationForVoucherAndCreditCardReverseInvoicePayment(ObjectContext context) {
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 21 records.", 21, queuedRecords.size());
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
		assertEquals("Not all InvoiceLines found in a  queue", 4, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 2, enrolmentsFound);
		assertEquals("Not all Vouchers found in a  queue", 1, vouchersFound);
		assertEquals("Not all Contacts found in a  queue", 1, contactsFound);
		assertEquals("Not all Students found in a  queue", 1, studentsFound);
	}

	protected void testFailedGUICases() throws Exception {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		checkQueueBeforeProcessing(context);

		GenericTransactionGroup transaction = processPayment();

		String sessionId = checkResponseAndReceiveSessionId(transaction);

		checkQueueAfterProcessing(context);

		transaction = getPaymentStatus(sessionId);

		checkNotProcessedResponse(transaction);

		//call page processing
		testRenderPaymentPageWithReverseInvoice(sessionId);

		checkAsyncReplication(context);

		transaction = getPaymentStatus(sessionId);

		checkProcessedResponse(transaction);

		logout();
	}

	protected void testSuccessGUICases() throws Exception {
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

	protected final GenericTransactionGroup preparePaymentStructureForTwoEnrolmentsWithoutVoucher(GenericTransactionGroup transaction) {
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
}
