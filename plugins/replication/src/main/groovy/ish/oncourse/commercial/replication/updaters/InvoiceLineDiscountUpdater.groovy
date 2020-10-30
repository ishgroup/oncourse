/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.InvoiceLineDiscount
import ish.oncourse.webservices.v22.stubs.replication.InvoiceLineDiscountStub

/**
 */
class InvoiceLineDiscountUpdater extends AbstractAngelUpdater<InvoiceLineDiscountStub, InvoiceLineDiscount> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(InvoiceLineDiscountStub stub, InvoiceLineDiscount entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setDiscount(callback.updateRelationShip(stub.getDiscountId(), Discount.class))
		entity.setInvoiceLine(callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class))
	}

}
