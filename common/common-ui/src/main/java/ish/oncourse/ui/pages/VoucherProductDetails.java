package ish.oncourse.ui.pages;

import ish.oncourse.model.VoucherProduct;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class VoucherProductDetails {
	@Inject
	private Request request;
	
	@Property
	private VoucherProduct voucherProduct;
	
	void beginRender() {
		voucherProduct = (VoucherProduct) request.getAttribute(VoucherProduct.class.getSimpleName());
	}

    public String getVoucherProductDetailsTitle(){
		if(voucherProduct == null) {
			return "Voucher product Not Found";
		}
		return voucherProduct.getName();
	}
	
}
