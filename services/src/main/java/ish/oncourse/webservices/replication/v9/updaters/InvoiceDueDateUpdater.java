/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v9.updaters;

import ish.math.Money;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceDueDate;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v9.stubs.replication.InvoiceDueDateStub;

public class InvoiceDueDateUpdater extends AbstractWillowUpdater<InvoiceDueDateStub, InvoiceDueDate> {
	@Override
	protected void updateEntity(InvoiceDueDateStub stub, InvoiceDueDate entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setAmount(new Money(stub.getAmount()));
		entity.setDueDate(stub.getDueDate());
		entity.setInvoice(callback.updateRelationShip(stub.getInvoiceId(), Invoice.class));
	}
}
