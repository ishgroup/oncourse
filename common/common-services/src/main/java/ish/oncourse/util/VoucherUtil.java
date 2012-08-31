package ish.oncourse.util;

import java.math.BigDecimal;
import java.util.Date;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.VoucherStatus;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Student;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;
import ish.util.ProductUtil;
import ish.util.SecurityUtil;

public class VoucherUtil {
	
	public static Voucher createVoucher(final VoucherProduct voucherProduct, final Money voucherPrice, final PaymentSource source, 
		final Contact contact) {
		final Voucher voucher = voucherProduct.getObjectContext().newObject(Voucher.class);
		voucher.setCode(SecurityUtil.generateRandomPassword(Voucher.VOUCHER_CODE_LENGTH));
		voucher.setCollege(voucherProduct.getCollege());
		if (contact != null) {
			voucher.setContact((Contact) voucherProduct.getObjectContext().localObject(contact.getObjectId(), null));
		}
		voucher.setContact(contact);
		voucher.setExpiryDate(ProductUtil.calculateExpiryDate(new Date(), voucherProduct.getExpiryType(), voucherProduct.getExpiryDays()));
		if (voucherProduct.getPriceExTax() != null) {
			voucher.setRedemptionValue(voucherProduct.getPriceExTax());
		} else {
			voucher.setRedemptionValue(voucherPrice);
		}
		voucher.setSource(source);
		voucher.setStatus(VoucherStatus.ACTIVE);
		voucher.setProduct(voucherProduct);
		voucher.setRedeemedCoursesCount(0);
		return voucher;
	}
	
	public static Voucher createVoucher(final VoucherProduct voucherProduct, final Money voucherPrice, final PaymentSource source) {
		return createVoucher(voucherProduct, voucherPrice, source, null);
	}
	
	public static Invoice createInvoiceForVoucher(final Voucher voucher, final Contact payer) {
		final Invoice invoice = voucher.getObjectContext().newObject(Invoice.class);
		// fill the invoice with default values
        invoice.setInvoiceDate(new Date());
        invoice.setAmountOwing(BigDecimal.ZERO);
        invoice.setDateDue(new Date());
        invoice.setSource(PaymentSource.SOURCE_WEB);
        invoice.setCollege(voucher.getCollege());
        //fill other data
        invoice.setContact((Contact) voucher.getObjectContext().localObject(payer.getObjectId(), null));
        invoice.setBillToAddress(invoice.getContact().getAddress());
        invoice.setDescription(String.format("Invoice for voucher %s", voucher.getProduct().getName()));
        invoice.setSource(voucher.getSource());
        invoice.setTotalExGst(voucher.getRedemptionValue().toBigDecimal());
        invoice.setTotalGst(voucher.getRedemptionValue().toBigDecimal());
        //fill invoiceLine
        final InvoiceLine invoiceLine = invoice.getObjectContext().newObject(InvoiceLine.class);
        invoiceLine.setCollege(invoice.getCollege());
        invoiceLine.setDescription(String.format("Voucher %s", voucher.getProduct().getDescription()));
        invoiceLine.setTitle(String.format("%s %s", payer.getFullName(), voucher.getProduct().getName()));
        invoiceLine.setPriceEachExTax(voucher.getRedemptionValue());
        invoiceLine.setDiscountEachExTax(Money.ZERO);
        invoiceLine.setTaxEach(Money.ZERO);
        invoiceLine.setQuantity(BigDecimal.ONE);
        invoiceLine.setInvoice(invoice);
        voucher.setInvoiceLine(invoiceLine);
		return invoice;
	}
	
	public static PaymentIn createPaymentInForVoucher(final Invoice invoice, final Student student, final PaymentType paymentType) {
		final PaymentIn paymentIn = invoice.getObjectContext().newObject(PaymentIn.class);
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		paymentIn.setSource(invoice.getSource());
		paymentIn.setCollege(invoice.getCollege());
		paymentIn.setAmount(invoice.getTotalExGst());
		paymentIn.setContact(invoice.getContact());
		paymentIn.setStudent((Student) invoice.getObjectContext().localObject(student.getObjectId(), null));
		paymentIn.setType(paymentType);
		//fill paymentInLine
		final PaymentInLine paymentInLine = paymentIn.getObjectContext().newObject(PaymentInLine.class);
		paymentInLine.setAmount(paymentIn.getAmount());
		paymentInLine.setCollege(paymentIn.getCollege());
		paymentInLine.setInvoice(invoice);
		paymentInLine.setPaymentIn(paymentIn);
		return paymentIn;
	}
}
