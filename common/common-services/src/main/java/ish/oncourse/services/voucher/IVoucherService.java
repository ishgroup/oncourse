package ish.oncourse.services.voucher;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;

import java.util.List;

public interface IVoucherService {
	List<VoucherProduct> getAvailableVoucherProducts();
	
	List<Voucher> getAvailableVouchersForUser(final Contact contact);
	
	Voucher getVoucherByCode(final String code);
	
	PaymentIn preparePaymentInForVoucherPurchase(final VoucherProduct voucherProduct, final Money voucherPrice, final Contact payer, final Contact owner);

}
