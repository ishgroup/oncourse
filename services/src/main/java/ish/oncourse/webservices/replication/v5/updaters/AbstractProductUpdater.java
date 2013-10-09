package ish.oncourse.webservices.replication.v5.updaters;

import ish.math.Money;
import ish.oncourse.model.Product;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v4.updaters.UpdaterException;
import ish.oncourse.webservices.v5.stubs.replication.ProductStub;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractProductUpdater<S extends ProductStub, E extends Product> extends AbstractWillowUpdater<S, E> {

	public static final String ERROR_SKU_MESSAGE_TEMPLATE = "Product with class = %s angelId = %s and willowid = %s and empty SKU detected!";
	@Override
	protected void updateEntity(S stub, E entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setDescription(stub.getDescription());
		entity.setIsOnSale(stub.isIsOnSale());
		entity.setIsWebVisible(stub.isIsWebVisible());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setNotes(stub.getNotes());
		if (StringUtils.trimToNull(stub.getSku()) == null) {
			String message = String.format(ERROR_SKU_MESSAGE_TEMPLATE,
					entity.getClass().getSimpleName(),
					stub.getAngelId(),
					stub.getWillowId());
			throw new UpdaterException(message);
		}
		entity.setSku(stub.getSku());
		entity.setType(stub.getType());
		if (stub.getPriceExTax() != null) {
			entity.setPriceExTax(new Money(stub.getPriceExTax()));
		}
		if (stub.getTaxAdjustment() != null) {
			entity.setTaxAdjustment(new Money(stub.getTaxAdjustment()));
		}
		entity.setFeeGST(stub.getTaxAmount() != null ? Money.valueOf(stub.getTaxAmount()) : Money.ZERO);
	}
}
