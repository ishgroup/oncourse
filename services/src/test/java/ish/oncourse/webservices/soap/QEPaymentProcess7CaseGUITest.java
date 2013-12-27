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

public abstract class QEPaymentProcess7CaseGUITest extends QEPaymentProcessTest {

	protected void fillV6PaymentStubsForCases7(GenericTransactionGroup transaction) {
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
		PaymentInLineStub paymentLineStub = new PaymentInLineStub();
		paymentLineStub.setAngelId(1l);
		paymentLineStub.setAmount(hundredDollars.multiply(2l).toBigDecimal());//to match original (partially reversed) invoice amount
		paymentLineStub.setCreated(current);
		paymentLineStub.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub.setInvoiceId(10l);//link with original invoice
		paymentLineStub.setModified(current);
		paymentLineStub.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub);
		PaymentInLineStub paymentLineStub2 = new PaymentInLineStub();
		paymentLineStub2.setAngelId(2l);
		paymentLineStub2.setAmount(Money.ZERO.subtract(hundredDollars).toBigDecimal());//to match reverse invoice amount
		paymentLineStub2.setCreated(current);
		paymentLineStub2.setEntityIdentifier(PAYMENT_LINE_IDENTIFIER);
		paymentLineStub2.setInvoiceId(11l);//link with reverse invoice
		paymentLineStub2.setModified(current);
		paymentLineStub2.setPaymentInId(paymentInStub.getAngelId());
		stubs.add(paymentLineStub2);

		InvoiceLineStub invoiceLineStub2 = new InvoiceLineStub();
		invoiceLineStub2.setAngelId(2l);
		invoiceLineStub2.setCreated(current);
		invoiceLineStub2.setDescription(StringUtils.EMPTY);
		invoiceLineStub2.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub2.setInvoiceId(10l);
		invoiceLineStub2.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub2.setModified(current);
		invoiceLineStub2.setPriceEachExTax(hundredDollars.divide(new BigDecimal(4)).toBigDecimal());
		invoiceLineStub2.setQuantity(BigDecimal.ONE);
		invoiceLineStub2.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub2.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub2);

		InvoiceLineStub invoiceLineStub3 = new InvoiceLineStub();
		invoiceLineStub3.setAngelId(3l);
		invoiceLineStub3.setCreated(current);
		invoiceLineStub3.setDescription(StringUtils.EMPTY);
		invoiceLineStub3.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub3.setInvoiceId(10l);
		invoiceLineStub3.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub3.setModified(current);
		invoiceLineStub3.setPriceEachExTax(hundredDollars.divide(new BigDecimal(4)).toBigDecimal());
		invoiceLineStub3.setQuantity(BigDecimal.ONE);
		invoiceLineStub3.setTaxEach(BigDecimal.ZERO);
		invoiceLineStub3.setTitle(StringUtils.EMPTY);
		stubs.add(invoiceLineStub3);

		InvoiceLineStub invoiceLineStub4 = new InvoiceLineStub();
		invoiceLineStub4.setAngelId(4l);
		invoiceLineStub4.setCreated(current);
		invoiceLineStub4.setDescription(StringUtils.EMPTY);
		invoiceLineStub4.setDiscountEachExTax(BigDecimal.ZERO);
		invoiceLineStub4.setInvoiceId(10l);
		invoiceLineStub4.setEntityIdentifier(INVOICE_LINE_IDENTIFIER);
		invoiceLineStub4.setModified(current);
		invoiceLineStub4.setPriceEachExTax(hundredDollars.divide(new BigDecimal(4)).toBigDecimal());
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
		voucherStub.setRedemptionValue(hundredDollars.divide(new BigDecimal(4)).toBigDecimal());
		voucherStub.setSource(PaymentSource.SOURCE_ONCOURSE.getDatabaseValue());
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
		stubs.add(articleStub);

		assertNull("Payment sessionid should be empty before processing", paymentInStub.getSessionId());
	}
}
