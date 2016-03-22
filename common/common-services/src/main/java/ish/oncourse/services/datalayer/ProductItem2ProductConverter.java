package ish.oncourse.services.datalayer;

import ish.oncourse.model.ProductItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ish.oncourse.services.datalayer.DataLayerFactory.*;

public class ProductItem2ProductConverter extends AbstractProductConverter<ProductItem> {
	private final static Logger logger = LogManager.getLogger();


	private AbstractProductConverter<ish.oncourse.model.Product> productConverter;

	@Override
	public void convert() {

		productConverter = getProductFactory().getConverterBy(getValue().getProduct());
		super.convert();
		try {
			if (getValue().getContact() != null) {
				getProduct().userId = getValue().getContact().getId().toString();
			} else {
				getProduct().userId = getValue().getInvoiceLine().getInvoice().getContact().getId().toString();
			}
		} catch (Exception e) {
			logger.debug(e);
		}
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
