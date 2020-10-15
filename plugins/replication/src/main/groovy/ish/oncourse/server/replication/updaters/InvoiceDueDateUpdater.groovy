/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.math.Money
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceDueDate
import ish.oncourse.webservices.v21.stubs.replication.InvoiceDueDateStub
import ish.util.LocalDateUtils

class InvoiceDueDateUpdater extends AbstractAngelUpdater<InvoiceDueDateStub, InvoiceDueDate> {
	@Override
	protected void updateEntity(InvoiceDueDateStub stub, InvoiceDueDate entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setDueDate(LocalDateUtils.dateToValue(stub.getDueDate()))
		entity.setAmount(new Money(stub.getAmount()))
		entity.setInvoice(callback.updateRelationShip(stub.getInvoiceId(), Invoice.class))
	}
}
