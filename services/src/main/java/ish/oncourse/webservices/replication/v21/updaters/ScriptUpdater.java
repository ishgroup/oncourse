/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v21.updaters;

import ish.common.types.EntityEvent;
import ish.common.types.SystemEventType;
import ish.common.types.TriggerType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Script;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v21.stubs.replication.ScriptStub;

public class ScriptUpdater extends AbstractWillowUpdater<ScriptStub, Script> {
	@Override
	protected void updateEntity(ScriptStub stub, Script entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setEnabled(stub.isEnabled());
		entity.setEntityClass(stub.getEntityClass());
		entity.setSchedule(stub.getSchedule());
		entity.setScript(stub.getScript());

		entity.setTriggerType(TypesUtil.getEnumForDatabaseValue(stub.getTriggerType(), TriggerType.class));
		
		if (stub.getEntityEventType() != null) {
			entity.setEntityEventType(TypesUtil.getEnumForDatabaseValue(stub.getEntityEventType(), EntityEvent.class));
		}
		entity.setDescription(stub.getDescription());
		if (stub.getSystemEventType() != null) {
			entity.setSystemEventType(TypesUtil.getEnumForDatabaseValue(stub.getSystemEventType(), SystemEventType.class));
		}
	}
}

