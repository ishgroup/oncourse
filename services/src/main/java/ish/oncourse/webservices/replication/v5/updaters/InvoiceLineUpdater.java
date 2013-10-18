package ish.oncourse.webservices.replication.v5.updaters;

import ish.math.Money;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.util.CommonUtils;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.InvoiceLineStub;

public class InvoiceLineUpdater extends AbstractWillowUpdater<InvoiceLineStub, InvoiceLine> {

	@Override
	protected void updateEntity(InvoiceLineStub stub, InvoiceLine entity, RelationShipCallback callback) {
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
		entity.setSortOrder(stub.getSortOrder());

		// ugly hack to handle InvoiceLine.sortOrder logic without migration to new stubs version
		if (CommonUtils.compare(getCurrentCollegeAngelVersion(entity), CommonUtils.VERSION_5_0) >= 0) {
			entity.setSortOrder(stub.getSortOrder());
		}
	}
}
