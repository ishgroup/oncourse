package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.ProductItem;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.ProductItemStub;

public abstract class AbstractProductItemStubBuilder<E extends ProductItem, S extends ProductItemStub> extends AbstractWillowStubBuilder<E, S> {
	
	@Override
	protected S createFullStub(final E entity) {
		S stub = createStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setInvoiceLineId(entity.getInvoiceLine().getId());
		stub.setProductId(entity.getProduct().getId());
		stub.setStatus(entity.getStatus().getDatabaseValue());
		stub.setType(entity.getType());
		return stub;
	}

	protected abstract S createStub();
}
