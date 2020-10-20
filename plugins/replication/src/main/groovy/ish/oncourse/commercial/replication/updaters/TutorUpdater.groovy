/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.webservices.v21.stubs.replication.TutorStub

/**
 */
class TutorUpdater extends AbstractAngelUpdater<TutorStub, Tutor> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(TutorStub stub, Tutor entity, RelationShipCallback callback) {
		def contactId = stub.getContactId()
		entity.setContact(callback.updateRelationShip(contactId, Contact.class))
		entity.setCreatedOn(stub.getCreated())
		entity.setDateFinished(stub.getFinishDate())
		entity.setDateStarted(stub.getStartDate())
		entity.setModifiedOn(stub.getModified())
		entity.setResume(stub.getResumeTextile())
	}
}
