/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.InvoiceLineDiscount
import ish.oncourse.webservices.v22.stubs.replication.InvoiceLineDiscountStub

/**
 */
class InvoiceLineDiscountStubBuilder extends AbstractAngelStubBuilder<InvoiceLineDiscount, InvoiceLineDiscountStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected InvoiceLineDiscountStub createFullStub(InvoiceLineDiscount entity) {
		def stub = new InvoiceLineDiscountStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setDiscountId(entity.getDiscount().getId())
		stub.setInvoiceLineId(entity.getInvoiceLine().getId())
		return stub
	}

}
