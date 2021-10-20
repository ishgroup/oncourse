package ish.oncourse.webservices.replication.v23.updaters;

import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v23.stubs.replication.TutorAttendanceStub;

public class SessionTutorUpdater extends AbstractWillowUpdater<TutorAttendanceStub, SessionTutor> {

	@Override
	protected void updateEntity(TutorAttendanceStub stub, SessionTutor entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class));
		entity.setType(stub.getAttendanceType());
		Session session = callback.updateRelationShip(stub.getSessionId(), Session.class);
		entity.setSession(session);
		entity.setStartDate(session.getStartDate());
		entity.setEndDate(session.getEndDate());

	}
}
