package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.v4.stubs.replication.TutorAttendanceStub;

public class SessionTutorUpdater extends AbstractWillowUpdater<TutorAttendanceStub, SessionTutor> {

	@Override
	protected void updateEntity(TutorAttendanceStub stub, SessionTutor entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setSession(callback.updateRelationShip(stub.getSessionId(), Session.class));
		entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class));
		entity.setType(stub.getAttendanceType());
	}
}
