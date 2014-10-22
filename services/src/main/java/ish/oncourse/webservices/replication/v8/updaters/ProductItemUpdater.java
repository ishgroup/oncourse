package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.ProductItemStub;

public class ProductItemUpdater extends AbstractProductItemUpdater<ProductItemStub, ProductItem> {

	@Override
	protected void updateEntity(final ProductItemStub stub, final ProductItem entity, final RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		entity.setProduct(callback.updateRelationShip(stub.getProductId(), Product.class));
	}

}
