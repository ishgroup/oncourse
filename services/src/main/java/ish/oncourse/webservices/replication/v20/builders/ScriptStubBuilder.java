/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v20.builders;

import ish.oncourse.model.Script;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v20.stubs.replication.ScriptStub;

public class ScriptStubBuilder extends AbstractWillowStubBuilder<Script, ScriptStub> {
	@Override
	protected ScriptStub createFullStub(Script entity) {
		ScriptStub stub = new ScriptStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setEnabled(entity.getEnabled());
		stub.setEntityClass(entity.getEntityClass());
		stub.setSchedule(entity.getSchedule());
		stub.setScript(entity.getScript());
		stub.setTriggerType(entity.getTriggerType().getDatabaseValue());
		if (entity.getEntityEventType() != null) {
			stub.setEntityEventType(entity.getEntityEventType().getDatabaseValue());
		}
		stub.setDescription(entity.getDescription());
		stub.setSystemEventType(entity.getSystemEventType() != null ? entity.getSystemEventType().getDatabaseValue() : null);
		return stub;
	}
}
