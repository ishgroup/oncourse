package ish.oncourse.services.voucher;

import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;

import java.util.List;

public interface IVoucherService {
	List<Product> getAvailableProducts(Integer startDefault, Integer rowsDefault);
	
	Voucher getVoucherByCode(final String code);
	
	Voucher createVoucher(VoucherProduct voucherProduct);

	List<Product> loadByIds(List<Long> ids);
	
	List<Product> loadByIds(Object... ids);

	List<ProductItem> loadProductItemsByIds(List<Long> ids);
	
	Product getProductBySKU(String sku);

	Integer getProductCount();

    boolean isVoucherWithoutPrice(VoucherProduct product);
}
