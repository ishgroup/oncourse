package ish.oncourse.webservices.soap.v9;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.replication.services.PaymentServiceImpl;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericInvoiceStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v9.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v9.stubs.replication.InvoiceLineStub;
import ish.oncourse.webservices.v9.stubs.replication.InvoiceStub;
import ish.oncourse.webservices.v9.stubs.replication.PaymentInLineStub;
import ish.oncourse.webservices.v9.stubs.replication.PaymentInStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class QEDuplicateStudentEnrolmentNoGUITest extends QEVoucherRedeemNoGUITest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEDuplicateStudentEnrolmentNoGUITestDataSet.xml";

	@Override
	protected String getDataSetFile() {
		return DEFAULT_DATASET_XML;
	}

	@Override
	protected GenericTransactionGroup prepareStubsForReplication(GenericTransactionGroup transaction) {
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
		paymentInStub.setType(PaymentType.CASH.getDatabaseValue());
		paymentInStub.setEntityIdentifier(PAYMENT_IDENTIFIER);
		stubs.add(paymentInStub);

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

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 12 records.", 12, queuedRecords.size());
		int paymentsFound = 0, paymentLinesFound = 0, invoicesFound = 0, invoiceLinesFound = 0, enrolmentsFound = 0,
			contactsFound = 0, studentsFound = 0;

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
			} else if (CONTACT_IDENTIFIER.equals(record.getEntityIdentifier())){
				contactsFound++;
			} else if (STUDENT_IDENTIFIER.equals(record.getEntityIdentifier())){
				studentsFound++;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}

		assertEquals("Not all PaymentIns found in a queue", 2, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 3, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 2, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 2, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 1, enrolmentsFound);
		assertEquals("Not all Contacts found in a  queue", 1, contactsFound);
		assertEquals("Not all Students found in a  queue", 1, studentsFound);
	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) throws Exception {
		assertFalse("Get status call should not return empty response for payment in final status",
				transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("16 elements should be replicated for this payment", 16, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				GenericPaymentInStub paymentInStub = (GenericPaymentInStub) stub;
				if (stub.getAngelId() == null) {
					if (stub.getWillowId() == 2l) {
						assertEquals("This should be 0 amount internal payment", PaymentType.REVERSE.getDatabaseValue(), paymentInStub.getType());
						PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
						assertEquals("Payment status should be success", PaymentStatus.SUCCESS, status);
					} else {
						assertFalse(String.format("Unexpected PaymentIn with id= %s angelid=%s and status= %s found in a queue",
								stub.getWillowId(), stub.getAngelId(), paymentInStub.getStatus()), true);
					}
				} else if (stub.getAngelId() == 1l) {
					assertEquals("This should be 100$ amount cash payment", PaymentType.CASH.getDatabaseValue(), paymentInStub.getType());
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be failed", PaymentStatus.FAILED, status);
					assertEquals("Payment status should be failed", BeanUtils.getProperty(paymentInStub, "privateNotes"), String.format(PaymentServiceImpl.MESSAGE_activeEnrolmentExists, "Vladimir Putin", "AAV-1"));

				} else {
					assertFalse(String.format("Unexpected PaymentIn with id= %s angelid=%s and status= %s found in a queue",
							stub.getWillowId(), stub.getAngelId(), paymentInStub.getStatus()), true);
				}
			} else if (stub instanceof GenericEnrolmentStub) {
				if (stub.getAngelId() == 1l) {
					EnrolmentStatus status = EnrolmentStatus.valueOf(((GenericEnrolmentStub) stub).getStatus());
					assertEquals("Oncourse enrollment should be failed", EnrolmentStatus.FAILED, status);
				} else {
					assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(),
							((GenericEnrolmentStub)stub).getStatus()), true);
				}
			} else if (stub instanceof GenericInvoiceStub) {
				if (stub.getAngelId() == null && stub.getWillowId() == 2l) {
					assertEquals("Incorrect reverse invoice amount", ((InvoiceStub) stub).getTotalGst(), new BigDecimal("-100.00"));
				} else if (stub.getAngelId() == 10l) {
					assertEquals("Incorrect original invoice amount", ((InvoiceStub) stub).getTotalGst(), new BigDecimal("100.00"));
				} else {
					assertFalse(String.format("Unexpected Invoice with id= %s and angelid= %s found in a queue",
							stub.getWillowId(), stub.getAngelId()), true);
				}
			}
		}
	}


	@Test
	public void testQEFailedPayment() throws Exception {
		testNoGUICases();
	}
}
