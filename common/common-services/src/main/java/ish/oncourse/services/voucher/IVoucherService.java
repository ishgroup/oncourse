package ish.oncourse.services.voucher;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;

import java.util.List;

public interface IVoucherService {
	List<VoucherProduct> getAvailableVoucherProducts();
	
	List<Voucher> getAvailableVoucherProductsForUser(final Contact contact);
	
	Voucher getVoucherByCode(final String code);

}
