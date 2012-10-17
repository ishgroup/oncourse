package ish.oncourse.webservices.replication.v5.updaters;

import ish.oncourse.model.Module;
import ish.oncourse.model.Session;
import ish.oncourse.model.SessionModule;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.SessionModuleStub;

public class SessionModuleUpdater extends AbstractWillowUpdater<SessionModuleStub, SessionModule>{

	@Override
	protected void updateEntity(SessionModuleStub stub, SessionModule entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setModule(callback.updateRelationShip(stub.getModuleId(), Module.class));
		entity.setSession(callback.updateRelationShip(stub.getSessionId(), Session.class));
	}

}
