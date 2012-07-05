package ish.oncourse.webservices.replication.v5.updaters;

import ish.oncourse.model.Discount;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.InvoiceLineDiscountStub;

public class InvoiceLineDiscountUpdater extends AbstractWillowUpdater<InvoiceLineDiscountStub, InvoiceLineDiscount> {

	@Override
	protected void updateEntity(InvoiceLineDiscountStub stub, InvoiceLineDiscount entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		
		Discount discount = callback.updateRelationShip(stub.getDiscountId(), Discount.class);
		entity.setDiscount(discount);
		
		InvoiceLine invoiceLine = callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class);
		entity.setInvoiceLine(invoiceLine);
		entity.setModified(stub.getModified());
	}
}
