package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.webservices.v4.stubs.replication.ProductItemStub;

public class ProductItemUpdater extends AbstractWillowUpdater<ProductItemStub, ProductItem> {

	@Override
	protected void updateEntity(final ProductItemStub stub, final ProductItem entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		final InvoiceLine invoiceLine = callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class);
		entity.setInvoiceLine(invoiceLine);
		entity.setModified(stub.getModified());
		final Product product = callback.updateRelationShip(stub.getProductId(), Product.class);
		entity.setProduct(product);
		entity.setType(stub.getType());
	}

}
