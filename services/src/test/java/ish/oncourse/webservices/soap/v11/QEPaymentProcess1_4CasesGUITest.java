package ish.oncourse.webservices.soap.v11;

import ish.common.types.ConfirmationStatus;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.common.types.ProductType;
import ish.math.Money;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.replication.services.ReplicationUtils;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v11.stubs.replication.ArticleStub;
import ish.oncourse.webservices.v11.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v11.stubs.replication.InvoiceLineStub;
import ish.oncourse.webservices.v11.stubs.replication.InvoiceStub;
import ish.oncourse.webservices.v11.stubs.replication.MembershipStub;
import ish.oncourse.webservices.v11.stubs.replication.PaymentInLineStub;
import ish.oncourse.webservices.v11.stubs.replication.PaymentInStub;
import ish.oncourse.webservices.v11.stubs.replication.VoucherStub;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNull;

public abstract class QEPaymentProcess1_4CasesGUITest extends QEPaymentProcessTest {

	protected void fillv11PaymentStubs(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
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
        paymentInStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());

		parametersMap.getGenericEntry().add(createEntry(
				String.format("%s_%d", ReplicationUtils.getEntityName(PaymentIn.class),paymentInStub.getAngelId()),
				paymentInStub.getAngelId().toString()
		));

		stubs.add(paymentInStub);
		InvoiceStub invoiceStub = new InvoiceStub();
		invoiceStub.setContactId(1l);
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
		
		parametersMap.getGenericEntry().add(createEntry(
				String.format("%s_%d", ReplicationUtils.getEntityName(Invoice.class), invoiceStub.getAngelId()),
				invoiceStub.getAngelId().toString()
		));
		PaymentInLineStub paymentLineStub = new PaymentInLineStub();
		paymentLineStub.setAngelId(1l);
		paymentLineStub.setAmount(paymentInStub.getAmount());
		paymentLineStub.setCreated(current);
		paymentLineStub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub.setInvoiceId(invoiceStub.getAngelId());
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
		invoiceLineStub.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(4)));
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
		invoiceLineStub2.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(4)));
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
		invoiceLineStub3.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(4)));
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
		invoiceLineStub4.setPriceEachExTax(invoiceStub.getAmountOwing().divide(new BigDecimal(4)));
		invoiceLineStub4.setQuantity(BigDecimal.ONE);
		invoiceLineStub4.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub4.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub4);

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

		MembershipStub membershipStub = new MembershipStub();
		membershipStub.setAngelId(1l);
		membershipStub.setContactId(1l);
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
		voucherStub.setContactId(1l);
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
		voucherStub.setRedemptionValue(invoiceStub.getAmountOwing().divide(new BigDecimal(4)));
		voucherStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
        voucherStub.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND.getDatabaseValue());
		stubs.add(voucherStub);

		ArticleStub articleStub = new ArticleStub();
		articleStub.setAngelId(3l);
		articleStub.setContactId(1l);
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
}
