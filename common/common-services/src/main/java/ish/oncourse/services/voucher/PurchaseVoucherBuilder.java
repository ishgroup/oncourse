package ish.oncourse.services.voucher;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Student;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.util.VoucherUtil;

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
	private final Student payer;
	private final Student owner;
	private PaymentIn paymentIn;
	private Invoice invoice;
	private final PaymentType paymentType;
	
	public PurchaseVoucherBuilder(final VoucherProduct voucherProduct, final Money voucherPrice, final Student payer, final PaymentType paymentType, 
		final Student owner) {
		this.voucherProduct = voucherProduct;
		this.voucherPrice = voucherPrice;
		this.source = PaymentSource.SOURCE_WEB;
		this.payer = payer;
		this.owner = owner;
		this.paymentType = paymentType;
	}

	/**
	 * @return the voucher
	 */
	public Voucher getVoucher() {
		return voucher;
	}

	/**
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
		voucher = VoucherUtil.createVoucher(voucherProduct, voucherPrice, source, payer.equals(owner) ? null : owner.getContact());
		invoice = VoucherUtil.createInvoiceForVoucher(getVoucher(), payer.getContact());
		paymentIn = VoucherUtil.createPaymentInForVoucher(invoice, payer.equals(owner) ? payer : owner, paymentType);
		return paymentIn;
	}

}
