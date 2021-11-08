package ish.oncourse.webservices.replication.v25.updaters;

import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v25.stubs.replication.TutorAttendanceStub;

public class SessionTutorUpdater extends AbstractWillowUpdater<TutorAttendanceStub, SessionTutor> {

	@Override
	protected void updateEntity(TutorAttendanceStub stub, SessionTutor entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setSession(callback.updateRelationShip(stub.getSessionId(), Session.class));
		entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class));
		entity.setType(stub.getAttendanceType());
		entity.setStartDate(stub.getStartDate());
		entity.setEndDate(stub.getEndDate());
	}
}
