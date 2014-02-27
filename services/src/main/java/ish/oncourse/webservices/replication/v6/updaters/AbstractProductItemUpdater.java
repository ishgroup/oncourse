package ish.oncourse.webservices.replication.v6.updaters;

import ish.common.types.ProductStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.ProductItem;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.ProductItemStub;

public abstract class AbstractProductItemUpdater<S extends ProductItemStub, E extends ProductItem> extends AbstractWillowUpdater<S, E> {

	@Override
	protected void updateEntity(S stub, E entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setInvoiceLine(callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class));
		entity.setModified(stub.getModified());
		entity.setType(stub.getType());
		if (stub.getStatus() != null) {
			entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), ProductStatus.class));
		} else {
			//set default new status because this may be V4.1 product item and we need to have ability succeed them
			entity.setStatus(ProductStatus.NEW);
		}
	}
}
