/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.Preference
import ish.oncourse.webservices.v21.stubs.replication.PreferenceStub

/**
 */
class PreferenceUpdater extends AbstractAngelUpdater<PreferenceStub, Preference> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(PreferenceStub stub, Preference entity, RelationShipCallback callback) {

		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setName(stub.getName())
		entity.setValue(stub.getValue())
		entity.setValueString(stub.getValueString())
	}
}
