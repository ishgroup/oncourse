package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.ProductItem;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.ProductItemStub;

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
		stub.setConfirmationStatus(entity.getConfirmationStatus().getDatabaseValue());
		return stub;
	}

	protected abstract S createStub();
}
