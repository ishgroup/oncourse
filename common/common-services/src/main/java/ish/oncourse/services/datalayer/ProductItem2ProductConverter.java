package ish.oncourse.services.datalayer;

import ish.oncourse.model.ProductItem;

import static ish.oncourse.services.datalayer.DataLayerFactory.*;

public class ProductItem2ProductConverter extends AbstractProductConverter<ProductItem> {

	private AbstractProductConverter<ish.oncourse.model.Product> productConverter;

	@Override
	public void convert() {

		productConverter = getProductFactory().getConverterBy(getValue().getProduct());
		super.convert();
        getProduct().userId = getValue().getContact().getId().toString();
	}

	@Override
	protected ProductId getProductId() {
		return productConverter.getProductId();
	}

	@Override
	protected ProductCategory getCategory() {
		return productConverter.getCategory();
	}

	@Override
	protected Price getPrice() {
		return getProductFactory().getPriceBy(getValue().getInvoiceLine());
	}
}
