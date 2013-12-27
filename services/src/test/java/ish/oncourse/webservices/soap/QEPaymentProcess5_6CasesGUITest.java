package ish.oncourse.webservices.soap;

import ish.common.types.*;
import ish.math.Money;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v6.stubs.replication.*;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNull;

public abstract class QEPaymentProcess5_6CasesGUITest extends QEPaymentProcessTest {

	protected void fillV6PaymentStubs(GenericTransactionGroup transaction) {
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Money hundredDollars = new Money("100.00");
		final Date current = new Date();
		PaymentInStub paymentInStub = new PaymentInStub();
		paymentInStub.setAngelId(1l);
		paymentInStub.setAmount(hundredDollars.multiply(2).toBigDecimal());
		paymentInStub.setContactId(1l);
		paymentInStub.setCreated(current);
		paymentInStub.setModified(current);
		paymentInStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
		paymentInStub.setStatus(PaymentStatus.IN_TRANSACTION.getDatabaseValue());
		paymentInStub.setType(PaymentType.CREDIT_CARD.getDatabaseValue());
		paymentInStub.setEntityIdentifier(PAYMENT_IDENTIFIER);
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
		stubs.add(voucherStub);

		ArticleStub articleStub = new ArticleStub();
		articleStub.setAngelId(2l);
		articleStub.setContactId(1l);
		articleStub.setCreated(current);
		articleStub.setEntityIdentifier(ARTICLE_IDENTIFIER);
		articleStub.setInvoiceLineId(invoiceLineStub4.getAngelId());
		articleStub.setModified(current);
		articleStub.setProductId(3l);
		articleStub.setType(ProductType.ARTICLE.getDatabaseValue());
		articleStub.setStatus(ProductStatus.NEW.getDatabaseValue());
		stubs.add(articleStub);

		assertNull("Payment sessionid should be empty before processing", paymentInStub.getSessionId());
	}
}
