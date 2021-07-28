/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.AbstractInvoiceLine
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.webservices.v23.stubs.replication.InvoiceLineStub

/**
 */
class InvoiceLineStubBuilder extends AbstractAngelStubBuilder<AbstractInvoiceLine, InvoiceLineStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected InvoiceLineStub createFullStub(AbstractInvoiceLine entity) {
		def stub = new InvoiceLineStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setDescription(entity.getDescription())
		if (entity.getDiscountEachExTax() != null) {
			stub.setDiscountEachExTax(entity.getDiscountEachExTax().toBigDecimal())
		}
		if (entity.getEnrolment() != null) {
			stub.setEnrolmentId(entity.getEnrolment().getId())
		}
		stub.setInvoiceId(entity.getInvoice().getId())
		if (entity.getPriceEachExTax() != null) {
			stub.setPriceEachExTax(entity.getPriceEachExTax().toBigDecimal())
		}
		stub.setQuantity(entity.getQuantity())
		if (entity.getTaxEach() != null) {
			stub.setTaxEach(entity.getTaxEach().toBigDecimal())
		}
		stub.setTitle(entity.getTitle())
		stub.setUnit(entity.getUnit())
		stub.setSortOrder(entity.getSortOrder())

		if (entity.getCourseClass() != null) {
			stub.setCourseClassId(entity.getCourseClass().getId())
		}

		return stub
	}
}
