package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.Product;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.ProductStub;

public class ProductUpdater extends AbstractProductUpdater<ProductStub, Product> {

	@Override
	protected void updateEntity(final ProductStub stub, final Product entity, final RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
	}

}
