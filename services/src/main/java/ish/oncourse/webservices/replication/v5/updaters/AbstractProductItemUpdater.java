package ish.oncourse.webservices.replication.v5.updaters;

import ish.common.types.ProductStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.ProductItemStub;

public abstract class AbstractProductItemUpdater<S extends ProductItemStub, E extends ProductItem> extends AbstractWillowUpdater<S, E> {

	@Override
	protected void updateEntity(S stub, E entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		InvoiceLine invoiceLine = callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class);
		entity.setInvoiceLine(invoiceLine);
		entity.setModified(stub.getModified());
		Product product = callback.updateRelationShip(stub.getProductId(), Product.class);
		entity.setProduct(product);
		entity.setType(stub.getType());
		if (stub.getStatus() != null) {
			ProductStatus status = TypesUtil.getEnumForDatabaseValue(stub.getStatus(), ProductStatus.class);
			entity.setStatus(status);
		} else {
			//set default new status because this may be V4.1 product item and we need to have ability succeed them
			entity.setStatus(ProductStatus.NEW);
		}
	}
}
