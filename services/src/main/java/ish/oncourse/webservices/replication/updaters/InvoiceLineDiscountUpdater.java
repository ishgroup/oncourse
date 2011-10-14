package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Discount;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.webservices.v4.stubs.replication.InvoiceLineDiscountStub;

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
