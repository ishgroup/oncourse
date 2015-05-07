package ish.oncourse.services.voucher;

import ish.oncourse.model.*;

import java.util.List;

public interface IVoucherService {
	List<Product> getAvailableProducts();
	
	List<Product> getAvailableProducts(Integer startDefault, Integer rowsDefault);
	
	List<Voucher> getAvailableVouchersForUser(final Contact contact);
	
	Voucher getVoucherByCode(final String code);
	
	Voucher createVoucher(VoucherProduct voucherProduct, Contact contact);

	List<Product> loadByIds(List<Long> ids);
	
	List<Product> loadByIds(Object... ids);

	List<ProductItem> loadProductItemsByIds(List<Long> ids);
	
	Product loadAvailableVoucherProductBySKU(String sku);

	public Integer getProductCount();

	Product loadAvailableVoucherProductById(Long id);

    boolean isVoucherWithoutPrice(VoucherProduct product);
}
