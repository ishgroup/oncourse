package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.ProductItem;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.ProductItemStub;

public class ProductItemUpdater extends AbstractProductItemUpdater<ProductItemStub, ProductItem> {

	@Override
	protected void updateEntity(final ProductItemStub stub, final ProductItem entity, final RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
	}

}
