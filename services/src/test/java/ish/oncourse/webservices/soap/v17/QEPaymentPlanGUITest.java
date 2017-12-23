/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v17;

import ish.common.types.*;
import ish.math.Money;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.replication.services.ReplicationUtils;
import ish.oncourse.webservices.util.*;
import ish.oncourse.webservices.v17.stubs.replication.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public abstract class QEPaymentPlanGUITest extends QEPaymentProcessTest {
	
	protected void fillv17PaymentStubs(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
		
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Money hundredDollars = new Money("100.00");
		final Money fiftyDollars = new Money("50.00");
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
		paymentInStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(paymentInStub);
		
		parametersMap.getGenericEntry().add(createEntry(
				String.format("%s_%d", ReplicationUtils.getEntityName(PaymentIn.class),paymentInStub.getAngelId()),
				paymentInStub.getAngelId().toString()
		));


		InvoiceStub invoiceStub = new InvoiceStub();
		invoiceStub.setContactId(1l);
		invoiceStub.setAmountOwing(fiftyDollars.toBigDecimal());
		invoiceStub.setAngelId(1l);
		invoiceStub.setCreated(current);
		invoiceStub.setDateDue(current);
		invoiceStub.setEntityIdentifier(INVOICE_IDENTIFIER);
		invoiceStub.setInvoiceDate(current);
		invoiceStub.setInvoiceNumber(123l);
		invoiceStub.setModified(current);
		invoiceStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		invoiceStub.setTotalExGst(invoiceStub.getAmountOwing());
		invoiceStub.setTotalGst(invoiceStub.getAmountOwing());
		invoiceStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(invoiceStub);

		parametersMap.getGenericEntry().add(createEntry(
				String.format("%s_%d", ReplicationUtils.getEntityName(Invoice.class), invoiceStub.getAngelId()),
				invoiceStub.getAngelId().toString()
		));
		

		InvoiceStub invoiceStub2 = new InvoiceStub();
		invoiceStub2.setContactId(1l);
		invoiceStub2.setAmountOwing(hundredDollars.toBigDecimal());
		invoiceStub2.setAngelId(2l);
		invoiceStub2.setCreated(current);
		invoiceStub2.setDateDue(current);
		invoiceStub2.setEntityIdentifier(INVOICE_IDENTIFIER);
		invoiceStub2.setInvoiceDate(current);
		invoiceStub2.setInvoiceNumber(124l);
		invoiceStub2.setModified(current);
		invoiceStub2.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		invoiceStub2.setTotalExGst(invoiceStub2.getAmountOwing());
		invoiceStub2.setTotalGst(invoiceStub2.getAmountOwing());
		invoiceStub2.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(invoiceStub2);

		parametersMap.getGenericEntry().add(createEntry(
				String.format("%s_%d", ReplicationUtils.getEntityName(Invoice.class), invoiceStub2.getAngelId()),
				invoiceStub2.getAngelId().toString()
		));
		
		
		PaymentInLineStub paymentLineStub = new PaymentInLineStub();
		paymentLineStub.setAngelId(1l);
		paymentLineStub.setAmount(fiftyDollars.toBigDecimal());
		paymentLineStub.setCreated(current);
		paymentLineStub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub.setInvoiceId(invoiceStub.getAngelId());
		paymentLineStub.setModified(current);
		paymentLineStub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub);

		PaymentInLineStub paymentLineStub2 = new PaymentInLineStub();
		paymentLineStub2.setAngelId(2l);
		paymentLineStub2.setAmount(fiftyDollars.toBigDecimal());
		paymentLineStub2.setCreated(current);
		paymentLineStub2.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub2.setInvoiceId(invoiceStub2.getAngelId());
		paymentLineStub2.setModified(current);
		paymentLineStub2.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub2);

		InvoiceLineStub invoiceLineStub = new InvoiceLineStub();
		invoiceLineStub.setAngelId(1l);
		invoiceLineStub.setCreated(current);
		invoiceLineStub.setDescription(StringUtils.EMPTY);
		invoiceLineStub.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub.setModified(current);
		invoiceLineStub.setPriceEachExTax(invoiceStub.getAmountOwing());
		invoiceLineStub.setQuantity(BigDecimal.ONE);
		invoiceLineStub.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub);

		InvoiceLineStub invoiceLineStub2 = new InvoiceLineStub();
		invoiceLineStub2.setAngelId(2l);
		invoiceLineStub2.setCreated(current);
		invoiceLineStub2.setDescription(StringUtils.EMPTY);
		invoiceLineStub2.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub2.setInvoiceId(invoiceStub2.getAngelId());
		invoiceLineStub2.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub2.setModified(current);
		invoiceLineStub2.setPriceEachExTax(invoiceStub2.getAmountOwing());
		invoiceLineStub2.setQuantity(BigDecimal.ONE);
		invoiceLineStub2.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub2.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub2);

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
		enrolmentStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		//link the invoiceLine with enrolment
		invoiceLineStub.setEnrolmentId(enrolmentStub.getAngelId());
		stubs.add(enrolmentStub);

		parametersMap.getGenericEntry().add(createEntry(
				String.format("%s_%d", ReplicationUtils.getEntityName(Enrolment.class), enrolmentStub.getAngelId()),
				enrolmentStub.getAngelId().toString()
		));

		EnrolmentStub enrolmentStub2 = new EnrolmentStub();
		enrolmentStub2.setAngelId(2l);
		enrolmentStub2.setCourseClassId(2l);
		enrolmentStub2.setCreated(current);
		enrolmentStub2.setEntityIdentifier(ENROLMENT_IDENTIFIER);
		enrolmentStub2.setInvoiceLineId(invoiceLineStub2.getAngelId());
		enrolmentStub2.setModified(current);
		enrolmentStub2.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		enrolmentStub2.setStatus(EnrolmentStatus.IN_TRANSACTION.name());
		enrolmentStub2.setStudentId(1l);
		enrolmentStub2.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		//link the invoiceLine with enrolment
		invoiceLineStub2.setEnrolmentId(enrolmentStub2.getAngelId());
		stubs.add(enrolmentStub2);

		parametersMap.getGenericEntry().add(createEntry(
				String.format("%s_%d", ReplicationUtils.getEntityName(Enrolment.class), enrolmentStub2.getAngelId()),
				enrolmentStub2.getAngelId().toString()
		));

		assertNull("Payment sessionid should be empty before processing", paymentInStub.getSessionId());
	}

	@Override
	protected String checkResponseAndReceiveSessionId(GenericTransactionGroup transaction) {
		String sessionId = null;
		assertEquals("16 stubs should be in response for this processing", 16, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
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
			}
		}
		return sessionId;
	}

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		List<QueuedRecord> queuedRecords = ObjectSelect.query(QueuedRecord.class)
				.select(context);
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 19 records.", 19, queuedRecords.size());
		int paymentsFound = 0, paymentLinesFound = 0, invoicesFound = 0, invoiceLinesFound = 0,
				enrolmentsFound = 0, contactFound = 0;
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
			} else if (CONTACT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				contactFound++;
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}
		assertEquals("Not all PaymentIns found in a queue", 2, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 4, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 4, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 4, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 4, enrolmentsFound);
		assertEquals("Not all Contact found in a  queue", 1, contactFound);
	}
}
