package ish.oncourse.ui.components;

import ish.oncourse.model.Product;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class ProductsList {
	@Inject
	@Property
	private Request request;
	
	@Parameter
	private Integer productsCount;
	
	@Parameter
	private Integer itemIndex;
	
	@SuppressWarnings("all")
	@Parameter
	@Property
	private List<Product> products;
	
	@SuppressWarnings("all")
	@Property
	private Product product;
	
	public boolean isHasMoreItems() {
        return itemIndex < productsCount;
    }
	
	public String getSearchParamsStr() {
		StringBuilder result = new StringBuilder();
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
