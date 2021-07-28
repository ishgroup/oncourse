/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.math.Money
import ish.oncourse.server.cayenne.AbstractInvoiceLine
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.webservices.v23.stubs.replication.InvoiceLineStub

/**
 */
class InvoiceLineUpdater extends AbstractAngelUpdater<InvoiceLineStub, AbstractInvoiceLine> {
    /**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(InvoiceLineStub stub, AbstractInvoiceLine entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setDescription(stub.getDescription())
		entity.setDiscountEachExTax(new Money(stub.getDiscountEachExTax()))
		entity.setInvoice(callback.updateRelationShip(stub.getInvoiceId(), Invoice.class))
		entity.setModifiedOn(stub.getModified())
		entity.setPriceEachExTax(new Money(stub.getPriceEachExTax()))
		entity.setQuantity(stub.getQuantity())
		entity.setTaxEach(new Money(stub.getTaxEach()))
		entity.setTitle(stub.getTitle())
		entity.setUnit(stub.getUnit())
		entity.setSortOrder(stub.getSortOrder())
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class))

		if (stub.getEnrolmentId() != null) {
			entity.setEnrolment(callback.updateRelationShip(stub.getEnrolmentId(), Enrolment.class))
		}
        /**
         * We should set prepaidFeesRemaining only if Invoice is related to Enrolment or CourseClass
         */
        if (stub.getEnrolmentId() != null || stub.getCourseClassId() != null) {
			// set prepaid fees remaining value to full price only when invoice line is created
			if (entity.getId() == null) {
            	entity.setPrepaidFeesRemaining(entity.getFinalPriceToPayExTax())
			}
        } else {
			entity.setPrepaidFeesRemaining(Money.ZERO)
		}
	}

}
