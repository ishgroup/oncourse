package ish.oncourse.services.voucher;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Product;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;

import java.util.List;

public interface IVoucherService {
	List<Product> getAvailableProducts();
	
	List<Product> getAvailableProducts(Integer startDefault, Integer rowsDefault);
	
	List<Voucher> getAvailableVouchersForUser(final Contact contact);
	
	Voucher getVoucherByCode(final String code);
	
	PaymentIn preparePaymentInForVoucherPurchase(final VoucherProduct voucherProduct, final Money voucherPrice, final Contact payer, final Contact owner);
	
	List<Product> loadByIds(Object... ids);
	
	Product loadAvailableVoucherProductById(Long id);

}
