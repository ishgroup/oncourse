package ish.oncourse.ui.pages;

import ish.oncourse.components.ISHCommon;
import ish.oncourse.model.Product;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ProductDetails extends ISHCommon {
	@Inject
	private Request request;
	
	@Property
	private Product product;
	
	void beginRender() {
		product = (Product) request.getAttribute(Product.class.getSimpleName());
	}

    public String getProductDetailsTitle(){
		if (product == null) {
			return "Product Not Found";
		}
		return product.getName();
	}
	
}
