/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v9.builders;

import ish.oncourse.model.InvoiceDueDate;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v9.stubs.replication.InvoiceDueDateStub;

public class InvoiceDueDateStubBuilder extends AbstractWillowStubBuilder<InvoiceDueDate, InvoiceDueDateStub> {
	@Override
	protected InvoiceDueDateStub createFullStub(InvoiceDueDate entity) {
		InvoiceDueDateStub stub = new InvoiceDueDateStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setDueDate(entity.getDueDate());
		stub.setAmount(entity.getAmount().toBigDecimal());
		stub.setInvoiceId(entity.getInvoice().getId());
		return stub;
	}
}
