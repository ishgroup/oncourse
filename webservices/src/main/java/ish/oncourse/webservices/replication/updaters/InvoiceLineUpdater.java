package ish.oncourse.webservices.replication.updaters;

import ish.math.Money;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.webservices.v4.stubs.replication.InvoiceLineStub;

public class InvoiceLineUpdater extends
		AbstractWillowUpdater<InvoiceLineStub, InvoiceLine> {

	@Override
	protected void updateEntity(InvoiceLineStub stub, InvoiceLine entity,
			RelationShipCallback callback) {
			
		entity.setCreated(stub.getCreated());
		entity.setDescription(stub.getDescription());
		entity.setDiscountEachExTax(Money.valueOf(stub.getDiscountEachExTax()));
		entity.setInvoice(callback.updateRelationShip(stub.getInvoiceId(), Invoice.class));
		entity.setModified(stub.getModified());
		entity.setPriceEachExTax(Money.valueOf(stub.getPriceEachExTax()));
		entity.setQuantity(stub.getQuantity());
		entity.setTaxEach(Money.valueOf(stub.getTaxEach()));
		entity.setTitle(stub.getTitle());
		entity.setUnit(stub.getUnit());
	}
}
