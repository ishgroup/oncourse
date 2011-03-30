package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.SessionTutorStub;

import java.util.List;

public class SessionTutorUpdater extends AbstractWillowUpdater<SessionTutorStub, SessionTutor> {

	@SuppressWarnings("unchecked")
	@Override
	protected void updateEntity(SessionTutorStub stub, SessionTutor entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(college);
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setSession((Session) updateRelationShip(stub.getSessionId(), "Session", result));
		entity.setTutor((Tutor) updateRelationShip(stub.getTutorId(), "Tutor", result));
	}
}
