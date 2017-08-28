/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v16;

import ish.common.types.*;
import ish.math.Money;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.webservices.replication.services.ReplicationUtils;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.v16.stubs.replication.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class QEAbandonWebPayemnts extends QEPaymentProcessTest {

	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEAbandonWebPayemntsDataSet.xml";
	private Date current = new Date();


	@Before
	public void before() throws Exception {
		testEnv = new V16TestEnv(DEFAULT_DATASET_XML, Collections.singletonMap("[now]", current));
		testEnv.start();
	}

	@Test
	public void test() throws Exception {
		testEnv.getTestEnv().authenticate();
		// prepare the stubs for replication
		final GenericTransactionGroup transaction = PortHelper.createTransactionGroup(testEnv.getSupportedVersion());
		final GenericParametersMap parametersMap = PortHelper.createParametersMap(testEnv.getSupportedVersion());
		fillQERequest(transaction, parametersMap);
		testEnv.getPaymentPortType().processPayment(testEnv.castGenericTransactionGroup(transaction), testEnv.castGenericParametersMap(parametersMap));

		ObjectContext context = testEnv.getCayenneService().newContext();
		Invoice invoice = SelectById.query(Invoice.class, 10l).selectOne(context);
		assertEquals(EnrolmentStatus.FAILED, invoice.getInvoiceLines().get(0).getEnrolment().getStatus());
		assertEquals(Money.ZERO, invoice.getAmountOwing());

		assertEquals(2, invoice.getPaymentInLines().size());
		for (PaymentInLine pl: invoice.getPaymentInLines()) {
			switch (pl.getPaymentIn().getType()) {
				case CREDIT_CARD:
					assertEquals(PaymentStatus.FAILED,pl.getPaymentIn().getStatus());
					break;
				case REVERSE:
					assertEquals(PaymentStatus.SUCCESS,pl.getPaymentIn().getStatus());
					break;
				default: assertTrue("Unexpected payment", false);
			}
		}
		Enrolment enrolment  = ObjectSelect.query(Enrolment.class).where(Enrolment.ANGEL_ID.eq(1l)).selectOne(context);
		assertEquals(EnrolmentStatus.SUCCESS, enrolment.getStatus());
	}

	private void fillQERequest(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {

		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Money hundredDollars = new Money("100.00");
		

		ContactStub contactStub = new ContactStub();
		contactStub.setAngelId(10l);
		contactStub.setCreated(current);
		contactStub.setModified(current);
		contactStub.setFamilyName("name");
		contactStub.setGivenName("name");
		contactStub.setEmailAddress("email@address.com");
		contactStub.setMarketingViaEmailAllowed(true);
		contactStub.setMarketingViaPostAllowed(true);
		contactStub.setMarketingViaSMSAllowed(true);
		contactStub.setEntityIdentifier(CONTACT_IDENTIFIER);
		contactStub.setStudentId(10l);
		stubs.add(contactStub);

		StudentStub studentStub = new StudentStub();
		studentStub.setAngelId(10l);
		studentStub.setContactId(10l);
		studentStub.setCreated(current);
		studentStub.setModified(current);
		studentStub.setEntityIdentifier(STUDENT_IDENTIFIER);
		stubs.add(studentStub);

		PaymentInStub paymentInStub = new PaymentInStub();
		paymentInStub.setAngelId(1l);
		paymentInStub.setAmount(hundredDollars.toBigDecimal());
		paymentInStub.setContactId(10l);
		paymentInStub.setCreated(current);
		paymentInStub.setModified(current);
		paymentInStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		paymentInStub.setStatus(PaymentStatus.IN_TRANSACTION.getDatabaseValue());
		paymentInStub.setType(PaymentType.CASH.getDatabaseValue());
		paymentInStub.setEntityIdentifier(PAYMENT_IDENTIFIER);
		paymentInStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(paymentInStub);
		parametersMap.getGenericEntry().add(createEntry(
				String.format("%s_%d", ReplicationUtils.getEntityName(PaymentIn.class), paymentInStub.getAngelId()),
				paymentInStub.getAngelId().toString()
		));


		EnrolmentStub enrolmentStub = new EnrolmentStub();
		enrolmentStub.setAngelId(1l);
		enrolmentStub.setCourseClassId(1l);
		enrolmentStub.setCreated(current);
		enrolmentStub.setEntityIdentifier(ENROLMENT_IDENTIFIER);
		enrolmentStub.setInvoiceLineId(1l);
		enrolmentStub.setModified(current);
		enrolmentStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		enrolmentStub.setStatus(EnrolmentStatus.IN_TRANSACTION.name());
		enrolmentStub.setStudentId(10l);
		stubs.add(enrolmentStub);

		parametersMap.getGenericEntry().add(createEntry(String.format("%s_%d", ReplicationUtils.getEntityName(Enrolment.class), enrolmentStub.getAngelId()),
				enrolmentStub.getAngelId().toString()));

		InvoiceStub invoiceStub = new InvoiceStub();
		invoiceStub.setContactId(10l);
		invoiceStub.setAmountOwing(hundredDollars.toBigDecimal());
		invoiceStub.setAngelId(1l);
		invoiceStub.setCreated(current);
		invoiceStub.setDateDue(current);
		invoiceStub.setEntityIdentifier(INVOICE_IDENTIFIER);
		invoiceStub.setInvoiceDate(current);
		invoiceStub.setInvoiceNumber(123l);
		invoiceStub.setModified(current);
		invoiceStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		invoiceStub.setTotalExGst(new BigDecimal("90"));
		invoiceStub.setTotalGst(new BigDecimal("10"));
		invoiceStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());

		parametersMap.getGenericEntry().add(createEntry(
				String.format("%s_%d", ReplicationUtils.getEntityName(Invoice.class), invoiceStub.getAngelId()),
				invoiceStub.getAngelId().toString()
		));

		stubs.add(invoiceStub);
		PaymentInLineStub paymentLineStub = new PaymentInLineStub();
		paymentLineStub.setAngelId(1l);
		paymentLineStub.setAmount(hundredDollars.toBigDecimal());
		paymentLineStub.setCreated(current);
		paymentLineStub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub.setInvoiceId(invoiceStub.getAngelId());
		paymentLineStub.setModified(current);
		paymentLineStub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub);

		InvoiceLineStub invoiceLineStub0 = new InvoiceLineStub();
		invoiceLineStub0.setAngelId(1l);
		invoiceLineStub0.setEnrolmentId(1l);
		invoiceLineStub0.setCreated(current);
		invoiceLineStub0.setDescription(StringUtils.EMPTY);
		invoiceLineStub0.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub0.setInvoiceId(1l);
		invoiceLineStub0.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub0.setModified(current);
		invoiceLineStub0.setPriceEachExTax(new BigDecimal("90"));
		invoiceLineStub0.setQuantity(BigDecimal.ONE);
		invoiceLineStub0.setTaxEach(new BigDecimal("10"));
		invoiceLineStub0.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub0);
	}
}
