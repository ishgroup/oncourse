package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Product;
import ish.oncourse.webservices.v4.stubs.replication.ProductStub;

public class ProductUpdater extends AbstractWillowUpdater<ProductStub, Product> {

	@Override
	protected void updateEntity(final ProductStub stub, final Product entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setDescription(stub.getDescription());
		entity.setIsOnSale(stub.isIsOnSale());
		entity.setIsWebVisible(stub.isIsWebVisible());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setNotes(stub.getNotes());
		entity.setSku(stub.getSku());
		entity.setType(stub.getType());		
	}

}
