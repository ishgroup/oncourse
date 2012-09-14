package ish.oncourse.ui.components;

import ish.oncourse.model.VoucherProduct;

import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class VoucherProductsList {
	@Inject
	@Property
	private Request request;
	
	@Parameter
	private Integer voucherProductsCount;
	
	@Parameter
	private Integer itemIndex;
	
	@SuppressWarnings("all")
	@Parameter
	@Property
	private List<VoucherProduct> voucherProducts;
	
	@SuppressWarnings("all")
	@Property
	private VoucherProduct voucherProduct;
	
	public boolean isHasMoreItems() {
        return itemIndex < voucherProductsCount;
    }
	
	public String getSearchParamsStr() {
		StringBuffer result = new StringBuffer();
		result.append("start=").append(itemIndex);
		for (String paramName : request.getParameterNames()) {
			if (!paramName.equals("start") ) {
				result.append("&");
				result.append(paramName).append("=");
				result.append(request.getParameter(paramName));
			}
		}
		return result.toString();
	}

}
