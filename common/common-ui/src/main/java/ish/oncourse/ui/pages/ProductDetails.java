package ish.oncourse.ui.pages;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.Product;
import org.apache.tapestry5.annotations.Property;

public class ProductDetails extends ISHCommon {
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
