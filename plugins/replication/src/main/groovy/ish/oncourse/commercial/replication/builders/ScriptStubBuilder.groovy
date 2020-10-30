/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Script
import ish.oncourse.webservices.v22.stubs.replication.ScriptStub

class ScriptStubBuilder extends AbstractAngelStubBuilder<Script, ScriptStub> {
	@Override
	protected ScriptStub createFullStub(Script entity) {
		def stub = new ScriptStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setName(entity.getName())
		stub.setEnabled(entity.getEnabled())
		stub.setEntityClass(entity.getEntityClass())
		stub.setSchedule(entity.getCronSchedule())
		stub.setScript(entity.getScript())
		stub.setTriggerType(entity.getTriggerType().getDatabaseValue())
		if (entity.getEntityEventType() != null) {
			stub.setEntityEventType(entity.getEntityEventType().getDatabaseValue())
		}
		if (entity.getSystemEventType() != null) {
			stub.setSystemEventType(entity.getSystemEventType().getDatabaseValue())
		}
		stub.setDescription(entity.getDescription())

		return stub
	}
}
