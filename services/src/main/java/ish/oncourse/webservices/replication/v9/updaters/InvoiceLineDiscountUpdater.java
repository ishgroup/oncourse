package ish.oncourse.webservices.replication.v9.updaters;

import ish.oncourse.model.Discount;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v9.stubs.replication.InvoiceLineDiscountStub;

public class InvoiceLineDiscountUpdater extends AbstractWillowUpdater<InvoiceLineDiscountStub, InvoiceLineDiscount> {

	@Override
	protected void updateEntity(InvoiceLineDiscountStub stub, InvoiceLineDiscount entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setDiscount(callback.updateRelationShip(stub.getDiscountId(), Discount.class));
		entity.setInvoiceLine(callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class));
		entity.setModified(stub.getModified());
	}
}
