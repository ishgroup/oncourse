/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.SessionModule
import ish.oncourse.webservices.v21.stubs.replication.SessionModuleStub

/**
 */
class SessionModuleStubBuilder extends AbstractAngelStubBuilder<SessionModule, SessionModuleStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected SessionModuleStub createFullStub(SessionModule entity) {
		def sessionModuleStub = new SessionModuleStub()
		sessionModuleStub.setModified(entity.getModifiedOn())
		sessionModuleStub.setCreated(entity.getCreatedOn())
		sessionModuleStub.setModuleId(entity.getModule().getWillowId())
		sessionModuleStub.setSessionId(entity.getSession().getId())

		return sessionModuleStub
	}
}
