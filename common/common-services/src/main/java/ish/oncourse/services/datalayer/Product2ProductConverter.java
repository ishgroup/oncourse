package ish.oncourse.services.datalayer;

import ish.oncourse.model.Product;
import ish.oncourse.util.HTMLUtils;
import org.apache.commons.lang.StringUtils;

import static ish.oncourse.services.datalayer.DataLayerFactory.CategoryType.PRODUCT;

public class Product2ProductConverter extends AbstractProductConverter<Product> {

	@Override
	protected DataLayerFactory.ProductId getProductId() {
		DataLayerFactory.ProductId productId = new DataLayerFactory.ProductId();
		productId.id = getValue().getId().toString();
		productId.name = getValue().getName();
		productId.url = HTMLUtils.getCanonicalLinkPathFor(getValue(), getProductFactory().getRequest());
		productId.sku = getValue().getSku();
		return productId;
	}

	@Override
	protected DataLayerFactory.ProductCategory getCategory() {
		DataLayerFactory.ProductCategory productCategory = new DataLayerFactory.ProductCategory();
		productCategory.primary = StringUtils.EMPTY;
		productCategory.type = PRODUCT.name();
		return productCategory;
	}


	@Override
	protected DataLayerFactory.Price getPrice() {
		DataLayerFactory.Price price = new DataLayerFactory.Price();
		price.base = getValue().getPriceExTax();
		price.withTax = getValue().getPriceIncTax();
		price.tax = getValue().getFeeGST();
		price.total = price.withTax;
		return price;
	}
}
