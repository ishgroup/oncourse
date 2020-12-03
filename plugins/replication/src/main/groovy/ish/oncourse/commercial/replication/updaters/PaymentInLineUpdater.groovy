/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.math.Money
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.webservices.v23.stubs.replication.PaymentInLineStub

/**
 */
class PaymentInLineUpdater extends AbstractAngelUpdater<PaymentInLineStub, PaymentInLine> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(PaymentInLineStub stub, PaymentInLine entity, RelationShipCallback callback) {
		entity.setAmount(new Money(stub.getAmount()))
		entity.setCreatedOn(stub.getCreated())
		def invoice = callback.updateRelationShip(stub.getInvoiceId(), Invoice.class)
		if (invoice != null) {
			entity.setInvoice(invoice)
			entity.setAccount(invoice.getDebtorsAccount())
		}
		entity.setModifiedOn(stub.getModified())
		entity.setPaymentIn(callback.updateRelationShip(stub.getPaymentInId(), PaymentIn.class))
	}

}
