/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.common.types.EntityEvent
import ish.common.types.SystemEventType
import ish.common.types.TriggerType
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.Script
import ish.oncourse.webservices.v23.stubs.replication.ScriptStub

class ScriptUpdater extends AbstractAngelUpdater<ScriptStub, Script> {
	@Override
	protected void updateEntity(ScriptStub stub, Script entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setName(stub.getName())
		entity.setEnabled(stub.isEnabled())
		entity.setEntityClass(stub.getEntityClass())
		entity.setCronSchedule(stub.getSchedule())
		entity.setScript(stub.getScript())

		entity.setTriggerType(TypesUtil.getEnumForDatabaseValue(stub.getTriggerType(), TriggerType.class))

		if (stub.getEntityEventType() != null) {
			entity.setEntityEventType(TypesUtil.getEnumForDatabaseValue(stub.getEntityEventType(), EntityEvent.class))
		}
		entity.setDescription(stub.getDescription())
		if (stub.getSystemEventType() != null) {
			entity.setSystemEventType(TypesUtil.getEnumForDatabaseValue(stub.getSystemEventType(), SystemEventType.class))
		}
	}
}
