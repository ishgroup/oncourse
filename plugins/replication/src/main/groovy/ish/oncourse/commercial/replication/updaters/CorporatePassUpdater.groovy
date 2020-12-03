/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.webservices.v23.stubs.replication.CorporatePassStub

/**
 */
class CorporatePassUpdater extends AbstractAngelUpdater<CorporatePassStub, CorporatePass> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(CorporatePassStub stub, CorporatePass entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setExpiryDate(stub.getExpiryDate())
		entity.setInvoiceEmail(stub.getInvoiceEmail())
		entity.setPassword(stub.getPassword())
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class))
	}

}
