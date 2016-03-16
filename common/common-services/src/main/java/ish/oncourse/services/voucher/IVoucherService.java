package ish.oncourse.services.voucher;

import ish.oncourse.model.*;

import java.util.List;

public interface IVoucherService {
	List<Product> getAvailableProducts(Integer startDefault, Integer rowsDefault);
	
	Voucher getVoucherByCode(final String code);
	
	Voucher createVoucher(VoucherProduct voucherProduct, Contact contact);

	List<Product> loadByIds(List<Long> ids);
	
	List<Product> loadByIds(Object... ids);

	List<ProductItem> loadProductItemsByIds(List<Long> ids);
	
	Product getProductBySKU(String sku);

	Integer getProductCount();

    boolean isVoucherWithoutPrice(VoucherProduct product);
}
