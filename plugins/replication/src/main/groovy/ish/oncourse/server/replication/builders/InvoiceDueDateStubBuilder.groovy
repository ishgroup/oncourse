/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.InvoiceDueDate
import ish.oncourse.webservices.v21.stubs.replication.InvoiceDueDateStub
import ish.util.LocalDateUtils

import java.util.Date

class InvoiceDueDateStubBuilder extends AbstractAngelStubBuilder<InvoiceDueDate, InvoiceDueDateStub>  {
	@Override
	protected InvoiceDueDateStub createFullStub(InvoiceDueDate entity) {
		def stub = new InvoiceDueDateStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		def dueDate = LocalDateUtils.valueToDateAtNoon(entity.getDueDate())
		stub.setDueDate(dueDate)
		stub.setAmount(entity.getAmount().toBigDecimal())
		stub.setInvoiceId(entity.getInvoice().getId())
		return stub
	}
}
