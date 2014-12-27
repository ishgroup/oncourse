package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.Module;
import ish.oncourse.model.Session;
import ish.oncourse.model.SessionModule;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.SessionModuleStub;
import org.apache.cayenne.Cayenne;

public class SessionModuleUpdater extends AbstractWillowUpdater<SessionModuleStub, SessionModule>{

	@Override
	protected void updateEntity(SessionModuleStub stub, SessionModule entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		if (stub.getModuleId() != null) {
			entity.setModule(Cayenne.objectForPK(entity.getObjectContext(), Module.class, stub.getModuleId()));
		}
		entity.setSession(callback.updateRelationShip(stub.getSessionId(), Session.class));
	}

}
