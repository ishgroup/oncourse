package ish.oncourse.services.voucher;

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

/**
 * Builder which prepare objects for voucher purchase
 * @author vdavidovich
 *
 */

public class PurchaseVoucherBuilder {
	private final VoucherProduct voucherProduct;
	private final Money voucherPrice;
	private final PaymentSource source;
	private Voucher voucher;
	private final Contact payer;
	private final Contact owner;
	private PaymentIn paymentIn;
	private Invoice invoice;
	private final PaymentType paymentType;
	
	/**
	 * Constructor with all required data to start builder.
	 * @param voucherProduct - the product which will be linked with the voucher.
	 * @param voucherPrice - the voucher price (will be ignored if the product have own priceExTax)
	 * @param payer - the contact which want to purchase the voucher
	 * @param owner - the contact which will be linked with the created voucher (if payer and owner is not the same person)
	 */
	public PurchaseVoucherBuilder(VoucherProduct voucherProduct, Money voucherPrice, Contact payer, Contact owner) {
		this.voucherProduct = voucherProduct;
		this.voucherPrice = voucherPrice;
		this.source = PaymentSource.SOURCE_WEB;
		this.payer = payer;
		this.owner = owner;
		this.paymentType = PaymentType.CREDIT_CARD;
	}

	/**
	 * Return the prepared voucher after building.
	 * @return the voucher
	 */
	public Voucher getVoucher() {
		return voucher;
	}

	/**
	 * Return the payment for voucher purchase.
	 * @return the paymentIn
	 */
	public PaymentIn getPaymentIn() {
		return paymentIn;
	}
	
	/**
	 * Prepare objects for voucher purchase.
	 * @return payment for voucher purchase.
	 */
	public PaymentIn prepareVoucherPurchase() {
		if (payer == null || voucherProduct == null) {
			throw new IllegalArgumentException("Payer or product objects required to be not null");
		}
		voucher = createVoucher(voucherProduct, voucherPrice, source, (owner == null || payer.equals(owner)) ? null : owner);
		invoice = createInvoiceForVoucher(getVoucher(), payer);
		paymentIn = createPaymentInForVoucher(invoice, (owner == null || payer.equals(owner)) ? payer : owner, paymentType);
		return paymentIn;
	}
	
	private static Voucher createVoucher(VoucherProduct voucherProduct, Money voucherPrice, PaymentSource source, Contact contact) {
		Voucher voucher = voucherProduct.getObjectContext().newObject(Voucher.class);
		voucher.setCode(SecurityUtil.generateRandomPassword(Voucher.VOUCHER_CODE_LENGTH));
		voucher.setCollege(voucherProduct.getCollege());
		if (contact != null) {
			voucher.setContact((Contact) voucherProduct.getObjectContext().localObject(contact.getObjectId(), null));
		}
		voucher.setExpiryDate(ProductUtil.calculateExpiryDate(new Date(), voucherProduct.getExpiryType(), voucherProduct.getExpiryDays()));
		if (!Money.isZeroOrEmpty(voucherProduct.getPriceExTax()) && Money.ZERO.isLessThan(voucherProduct.getPriceExTax())) {
			voucher.setRedemptionValue(voucherProduct.getPriceExTax());
		} else if (!Money.isZeroOrEmpty(voucherPrice) && Money.ZERO.isLessThan(voucherPrice)){
			voucher.setRedemptionValue(voucherPrice);
		} else {
			throw new IllegalArgumentException("Voucher price can't be null, zero or negative when we purchase voucher.");
		}
		voucher.setSource(source);
		voucher.setStatus(VoucherStatus.ACTIVE);
		voucher.setProduct(voucherProduct);
		voucher.setRedeemedCoursesCount(0);
		
		return voucher;
	}
	
	private static Invoice createInvoiceForVoucher(Voucher voucher, Contact payer) {
		Invoice invoice = voucher.getObjectContext().newObject(Invoice.class);
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
        InvoiceLine invoiceLine = invoice.getObjectContext().newObject(InvoiceLine.class);
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
	
	private static PaymentIn createPaymentInForVoucher(Invoice invoice, Contact owner, PaymentType paymentType) {
		PaymentIn paymentIn = invoice.getObjectContext().newObject(PaymentIn.class);
		paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
		paymentIn.setSource(invoice.getSource());
		paymentIn.setCollege(invoice.getCollege());
		paymentIn.setAmount(invoice.getTotalGst());
		paymentIn.setContact(invoice.getContact());
		if (owner.getStudent() != null) {
			paymentIn.setStudent((Student) invoice.getObjectContext().localObject(owner.getStudent().getObjectId(), null));
		}
		paymentIn.setType(paymentType);
		//fill paymentInLine
		PaymentInLine paymentInLine = paymentIn.getObjectContext().newObject(PaymentInLine.class);
		paymentInLine.setAmount(paymentIn.getAmount());
		paymentInLine.setCollege(paymentIn.getCollege());
		paymentInLine.setInvoice(invoice);
		paymentInLine.setPaymentIn(paymentIn);
		return paymentIn;
	}

}
