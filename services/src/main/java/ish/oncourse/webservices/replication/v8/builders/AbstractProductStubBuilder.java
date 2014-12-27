package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.Product;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.ProductStub;

public abstract class AbstractProductStubBuilder<E extends Product, S extends ProductStub> extends AbstractWillowStubBuilder<E, S> {

	@Override
	protected S createFullStub(final E entity) {
		S stub = createStub();
		stub.setCreated(entity.getCreated());
		stub.setDescription(entity.getDescription());
		stub.setIsOnSale(entity.getIsOnSale());
		stub.setIsWebVisible(entity.getIsWebVisible());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setNotes(entity.getNotes());
		if (entity.getPriceExTax() != null) {
			stub.setPriceExTax(entity.getPriceExTax().toBigDecimal());
		}
		stub.setSku(entity.getSku());
		if (entity.getTaxAdjustment() != null) {
			stub.setTaxAdjustment(entity.getTaxAdjustment().toBigDecimal());
		}
		stub.setType(entity.getType());
		return stub;
	}

	protected abstract S createStub();
}
