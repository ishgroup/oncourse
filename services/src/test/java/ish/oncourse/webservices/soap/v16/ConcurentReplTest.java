/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
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
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNull;


public class ConcurentReplTest extends RealWSTransportTest {

	private static final Logger logger = LogManager.getLogger();

	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/ConcurentDataSet.xml";

	@Before
	public void before() throws Exception {
		testEnv = new V16TestEnv(DEFAULT_DATASET_XML, null);
		testEnv.start();
		Connection connection = testEnv.getTestEnv().getDataSource().getConnection();
		Statement statement = testEnv.getTestEnv().getDataSource().getConnection().createStatement();
		statement.execute("ALTER TABLE ENROLMENT ADD CONSTRAINT angel_college_unique UNIQUE(ANGELID, COLLEGEID)");
		statement.close();
		connection.commit();
	}

	private void fillv16PaymentStubs(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Money hundredDollars = new Money("100.00");
		final Date current = new Date();

		ContactStub contactStub = new ContactStub();
		contactStub.setAngelId(2l);
		contactStub.setCreated(current);
		contactStub.setModified(current);
		contactStub.setFamilyName("name");
		contactStub.setGivenName("name");
		contactStub.setEmailAddress("email@address.com");
		contactStub.setMarketingViaEmailAllowed(true);
		contactStub.setMarketingViaPostAllowed(true);
		contactStub.setMarketingViaSMSAllowed(true);
		contactStub.setEntityIdentifier(CONTACT_IDENTIFIER);
		contactStub.setStudentId(2l);
		stubs.add(contactStub);

		StudentStub studentStub = new StudentStub();
		studentStub.setAngelId(2l);
		studentStub.setContactId(2l);
		studentStub.setCreated(current);
		studentStub.setModified(current);
		studentStub.setEntityIdentifier(STUDENT_IDENTIFIER);
		stubs.add(studentStub);

		PaymentInStub paymentInStub = new PaymentInStub();
		paymentInStub.setAngelId(1l);
		paymentInStub.setAmount(hundredDollars.multiply(2).toBigDecimal());
		paymentInStub.setContactId(10l);
		paymentInStub.setCreated(current);
		paymentInStub.setModified(current);
		paymentInStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		paymentInStub.setStatus(PaymentStatus.IN_TRANSACTION.getDatabaseValue());
		paymentInStub.setType(PaymentType.CASH.getDatabaseValue());
		paymentInStub.setEntityIdentifier(PAYMENT_IDENTIFIER);
		paymentInStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(paymentInStub);
		parametersMap.getGenericEntry().add(testEnv.getTestEnv().createEntry(
				String.format("%s_%d", ReplicationUtils.getEntityName(PaymentIn.class), paymentInStub.getAngelId()),
				paymentInStub.getAngelId().toString()
		));


		EnrolmentStub enrolmentStub = new EnrolmentStub();
		enrolmentStub.setAngelId(1l);
		enrolmentStub.setCourseClassId(1l);
		enrolmentStub.setCreated(current);
		enrolmentStub.setEntityIdentifier(ENROLMENT_IDENTIFIER);
		enrolmentStub.setInvoiceLineId(5l);
		enrolmentStub.setModified(current);
		enrolmentStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		enrolmentStub.setStatus(EnrolmentStatus.IN_TRANSACTION.name());
		enrolmentStub.setStudentId(2l);
		stubs.add(enrolmentStub);

		parametersMap.getGenericEntry().add(testEnv.getTestEnv().createEntry(
				String.format("%s_%d", ReplicationUtils.getEntityName(Enrolment.class), enrolmentStub.getAngelId()),
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
		invoiceStub.setTotalExGst(invoiceStub.getAmountOwing());
		invoiceStub.setTotalGst(invoiceStub.getAmountOwing());
		invoiceStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());

		parametersMap.getGenericEntry().add(testEnv.getTestEnv().createEntry(
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
		PaymentInLineStub paymentLine2Stub = new PaymentInLineStub();
		paymentLine2Stub.setAngelId(2l);
		paymentLine2Stub.setAmount(hundredDollars.toBigDecimal());
		paymentLine2Stub.setCreated(current);
		paymentLine2Stub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLine2Stub.setInvoiceId(10l);
		paymentLine2Stub.setModified(current);
		paymentLine2Stub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLine2Stub);


		InvoiceLineStub invoiceLineStub0 = new InvoiceLineStub();
		invoiceLineStub0.setAngelId(5l);
		invoiceLineStub0.setEnrolmentId(1l);
		invoiceLineStub0.setCreated(current);
		invoiceLineStub0.setDescription(StringUtils.EMPTY);
		invoiceLineStub0.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub0.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub0.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub0.setModified(current);
		invoiceLineStub0.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		invoiceLineStub0.setQuantity(BigDecimal.ONE);
		invoiceLineStub0.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub0.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub0);

		InvoiceLineStub invoiceLineStub = new InvoiceLineStub();
		invoiceLineStub.setAngelId(1l);
		invoiceLineStub.setCreated(current);
		invoiceLineStub.setDescription(StringUtils.EMPTY);
		invoiceLineStub.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub.setModified(current);
		invoiceLineStub.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		invoiceLineStub.setQuantity(BigDecimal.ONE);
		invoiceLineStub.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub);

		InvoiceLineStub invoiceLineStub2 = new InvoiceLineStub();
		invoiceLineStub2.setAngelId(2l);
		invoiceLineStub2.setCreated(current);
		invoiceLineStub2.setDescription(StringUtils.EMPTY);
		invoiceLineStub2.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub2.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub2.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub2.setModified(current);
		invoiceLineStub2.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		invoiceLineStub2.setQuantity(BigDecimal.ONE);
		invoiceLineStub2.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub2.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub2);

		InvoiceLineStub invoiceLineStub3 = new InvoiceLineStub();
		invoiceLineStub3.setAngelId(3l);
		invoiceLineStub3.setCreated(current);
		invoiceLineStub3.setDescription(StringUtils.EMPTY);
		invoiceLineStub3.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub3.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub3.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub3.setModified(current);
		invoiceLineStub3.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		invoiceLineStub3.setQuantity(BigDecimal.ONE);
		invoiceLineStub3.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub3.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub3);

		InvoiceLineStub invoiceLineStub4 = new InvoiceLineStub();
		invoiceLineStub4.setAngelId(4l);
		invoiceLineStub4.setCreated(current);
		invoiceLineStub4.setDescription(StringUtils.EMPTY);
		invoiceLineStub4.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub4.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub4.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub4.setModified(current);
		invoiceLineStub4.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		invoiceLineStub4.setQuantity(BigDecimal.ONE);
		invoiceLineStub4.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub4.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub4);

		MembershipStub membershipStub = new MembershipStub();
		membershipStub.setAngelId(1l);
		membershipStub.setContactId(10l);
		membershipStub.setCreated(current);
		membershipStub.setEntityIdentifier(MEMBERSHIP_IDENTIFIER);
		membershipStub.setInvoiceLineId(invoiceLineStub2.getAngelId());
		membershipStub.setModified(current);
		membershipStub.setProductId(2l);
		membershipStub.setType(ProductType.MEMBERSHIP.getDatabaseValue());
		membershipStub.setStatus(ProductStatus.NEW.getDatabaseValue());
		membershipStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(membershipStub);

		VoucherStub voucherStub = new VoucherStub();
		voucherStub.setAngelId(2l);
		voucherStub.setContactId(10l);
		voucherStub.setCreated(current);
		voucherStub.setEntityIdentifier(VOUCHER_IDENTIFIER);
		voucherStub.setInvoiceLineId(invoiceLineStub3.getAngelId());
		voucherStub.setModified(current);
		voucherStub.setProductId(1l);
		voucherStub.setType(ProductType.VOUCHER.getDatabaseValue());
		voucherStub.setStatus(ProductStatus.NEW.getDatabaseValue());
		voucherStub.setCode("some code");
		voucherStub.setExpiryDate(current);
		voucherStub.setRedeemedCoursesCount(0);
		voucherStub.setRedemptionValue(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		voucherStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		voucherStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(voucherStub);

		ArticleStub articleStub = new ArticleStub();
		articleStub.setAngelId(3l);
		articleStub.setContactId(10l);
		articleStub.setCreated(current);
		articleStub.setEntityIdentifier(ARTICLE_IDENTIFIER);
		articleStub.setInvoiceLineId(invoiceLineStub4.getAngelId());
		articleStub.setModified(current);
		articleStub.setProductId(3l);
		articleStub.setType(ProductType.ARTICLE.getDatabaseValue());
		articleStub.setStatus(ProductStatus.NEW.getDatabaseValue());
		articleStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(articleStub);

		assertNull("Payment sessionid should be empty before processing", paymentInStub.getSessionId());
	}


	protected void fillv16ReplStubs(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Money hundredDollars = new Money("100.00");
		final Date current = new Date();

		ContactStub contactStub = new ContactStub();
		contactStub.setAngelId(2l);
		contactStub.setStudentId(2l);
		contactStub.setCreated(current);
		contactStub.setModified(current);
		contactStub.setFamilyName("name");
		contactStub.setGivenName("name");
		contactStub.setEmailAddress("email@address.com");
		contactStub.setMarketingViaEmailAllowed(true);
		contactStub.setMarketingViaPostAllowed(true);
		contactStub.setMarketingViaSMSAllowed(true);
		contactStub.setEntityIdentifier(CONTACT_IDENTIFIER);
		stubs.add(contactStub);

		StudentStub studentStub = new StudentStub();
		studentStub.setAngelId(2l);
		studentStub.setContactId(2l);
		studentStub.setCreated(current);
		studentStub.setModified(current);
		studentStub.setEntityIdentifier(STUDENT_IDENTIFIER);
		stubs.add(studentStub);

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
		invoiceStub.setTotalExGst(invoiceStub.getAmountOwing());
		invoiceStub.setTotalGst(invoiceStub.getAmountOwing());
		invoiceStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(invoiceStub);

		InvoiceLineStub invoiceLineStub0 = new InvoiceLineStub();
		invoiceLineStub0.setAngelId(5l);
		invoiceLineStub0.setEnrolmentId(1l);
		invoiceLineStub0.setCreated(current);
		invoiceLineStub0.setDescription(StringUtils.EMPTY);
		invoiceLineStub0.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub0.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub0.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub0.setModified(current);
		invoiceLineStub0.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		invoiceLineStub0.setQuantity(BigDecimal.ONE);
		invoiceLineStub0.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub0.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub0);

		InvoiceLineStub invoiceLineStub = new InvoiceLineStub();
		invoiceLineStub.setAngelId(1l);
		invoiceLineStub.setCreated(current);
		invoiceLineStub.setDescription(StringUtils.EMPTY);
		invoiceLineStub.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub.setModified(current);
		invoiceLineStub.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		invoiceLineStub.setQuantity(BigDecimal.ONE);
		invoiceLineStub.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub);

		InvoiceLineStub invoiceLineStub2 = new InvoiceLineStub();
		invoiceLineStub2.setAngelId(2l);
		invoiceLineStub2.setCreated(current);
		invoiceLineStub2.setDescription(StringUtils.EMPTY);
		invoiceLineStub2.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub2.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub2.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub2.setModified(current);
		invoiceLineStub2.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		invoiceLineStub2.setQuantity(BigDecimal.ONE);
		invoiceLineStub2.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub2.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub2);

		InvoiceLineStub invoiceLineStub3 = new InvoiceLineStub();
		invoiceLineStub3.setAngelId(3l);
		invoiceLineStub3.setCreated(current);
		invoiceLineStub3.setDescription(StringUtils.EMPTY);
		invoiceLineStub3.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub3.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub3.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub3.setModified(current);
		invoiceLineStub3.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		invoiceLineStub3.setQuantity(BigDecimal.ONE);
		invoiceLineStub3.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub3.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub3);

		InvoiceLineStub invoiceLineStub4 = new InvoiceLineStub();
		invoiceLineStub4.setAngelId(4l);
		invoiceLineStub4.setCreated(current);
		invoiceLineStub4.setDescription(StringUtils.EMPTY);
		invoiceLineStub4.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub4.setInvoiceId(invoiceStub.getAngelId());
		invoiceLineStub4.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub4.setModified(current);
		invoiceLineStub4.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		invoiceLineStub4.setQuantity(BigDecimal.ONE);
		invoiceLineStub4.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub4.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub4);

		EnrolmentStub enrolmentStub = new EnrolmentStub();
		enrolmentStub.setAngelId(1l);
		enrolmentStub.setCourseClassId(1l);
		enrolmentStub.setCreated(current);
		enrolmentStub.setEntityIdentifier(ENROLMENT_IDENTIFIER);
		enrolmentStub.setInvoiceLineId(invoiceLineStub0.getAngelId());
		enrolmentStub.setModified(current);
		enrolmentStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		enrolmentStub.setStatus(EnrolmentStatus.IN_TRANSACTION.name());
		enrolmentStub.setStudentId(2l);
		invoiceLineStub0.setEnrolmentId(enrolmentStub.getAngelId());
		stubs.add(enrolmentStub);


		MembershipStub membershipStub = new MembershipStub();
		membershipStub.setAngelId(1l);
		membershipStub.setContactId(10l);
		membershipStub.setCreated(current);
		membershipStub.setEntityIdentifier(MEMBERSHIP_IDENTIFIER);
		membershipStub.setInvoiceLineId(invoiceLineStub2.getAngelId());
		membershipStub.setModified(current);
		membershipStub.setProductId(2l);
		membershipStub.setType(ProductType.MEMBERSHIP.getDatabaseValue());
		membershipStub.setStatus(ProductStatus.NEW.getDatabaseValue());
		membershipStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(membershipStub);

		VoucherStub voucherStub = new VoucherStub();
		voucherStub.setAngelId(2l);
		voucherStub.setContactId(10l);
		voucherStub.setCreated(current);
		voucherStub.setEntityIdentifier(VOUCHER_IDENTIFIER);
		voucherStub.setInvoiceLineId(invoiceLineStub3.getAngelId());
		voucherStub.setModified(current);
		voucherStub.setProductId(1l);
		voucherStub.setType(ProductType.VOUCHER.getDatabaseValue());
		voucherStub.setStatus(ProductStatus.NEW.getDatabaseValue());
		voucherStub.setCode("some code");
		voucherStub.setExpiryDate(current);
		voucherStub.setRedeemedCoursesCount(0);
		voucherStub.setRedemptionValue(invoiceStub.getAmountOwing().divide(new BigDecimal(5)));
		voucherStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		voucherStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(voucherStub);

		ArticleStub articleStub = new ArticleStub();
		articleStub.setAngelId(3l);
		articleStub.setContactId(10l);
		articleStub.setCreated(current);
		articleStub.setEntityIdentifier(ARTICLE_IDENTIFIER);
		articleStub.setInvoiceLineId(invoiceLineStub4.getAngelId());
		articleStub.setModified(current);
		articleStub.setProductId(3l);
		articleStub.setType(ProductType.ARTICLE.getDatabaseValue());
		articleStub.setStatus(ProductStatus.NEW.getDatabaseValue());
		articleStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(articleStub);
	}

	@Test
	public void testConcurentProcessing() throws Exception {

		ExecutorService asyncThreadExecutor = Executors.newFixedThreadPool(3);

		testEnv.getTestEnv().authenticate();
		// prepare the stubs for replication
		final GenericTransactionGroup transaction = PortHelper.createTransactionGroup(testEnv.getTestEnv().getSupportedVersion());
		final GenericParametersMap parametersMap = PortHelper.createParametersMap(testEnv.getTestEnv().getSupportedVersion());
		fillv16PaymentStubs(transaction, parametersMap);

		final ReplicationRecords replicationRequest = new ReplicationRecords();
		GenericTransactionGroup group = new TransactionGroup();

		fillv16ReplStubs(group);
		group.getTransactionKeys().add("tr_key");
		replicationRequest.getGroups().add((TransactionGroup) testEnv.getTestEnv().getTransportConfig().castGenericTransactionGroup(group));

		Callable async = () -> testEnv.getTestEnv().getTransportConfig().sendRecords(replicationRequest);

		Callable sync = () -> testEnv.getTestEnv().processPayment(transaction, parametersMap);

		Future<ReplicationResult> asyncFuture = asyncThreadExecutor.submit(async);
		Future<TransactionGroup> syncFuture = asyncThreadExecutor.submit(sync);

		asyncFuture.get();
		syncFuture.get();

	}

}
