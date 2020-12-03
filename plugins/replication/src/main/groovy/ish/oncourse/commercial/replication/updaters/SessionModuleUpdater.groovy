/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Module
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.SessionModule
import ish.oncourse.commercial.replication.reference.ReferenceUtil
import ish.oncourse.webservices.v23.stubs.replication.SessionModuleStub

/**
 */
class SessionModuleUpdater extends AbstractAngelUpdater<SessionModuleStub, SessionModule> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(SessionModuleStub stub, SessionModule entity, RelationShipCallback callback) {

		Module m = null
		def moduleId = stub.getModuleId()
		if (moduleId != null) {
			m = ReferenceUtil.findModuleByWillowId(entity.getObjectContext(), moduleId)
		}
		entity.setModule(m)

		entity.setSession(callback.updateRelationShip(stub.getSessionId(), Session.class))
	}

}
