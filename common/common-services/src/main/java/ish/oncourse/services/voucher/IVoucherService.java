package ish.oncourse.services.voucher;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Product;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;

import java.util.List;

public interface IVoucherService {
	List<Product> getAvailableProducts();
	
	List<Product> getAvailableProducts(Integer startDefault, Integer rowsDefault);
	
	List<Voucher> getAvailableVouchersForUser(final Contact contact);
	
	Voucher getVoucherByCode(final String code);
	
	Voucher createVoucher(VoucherProduct voucherProduct, Contact contact, Money voucherPrice);
	
	List<Product> loadByIds(Object... ids);
	
	Product loadAvailableVoucherProductBySKU(String sku);
	
	Product loadAvailableVoucherProductById(Long id);

}
