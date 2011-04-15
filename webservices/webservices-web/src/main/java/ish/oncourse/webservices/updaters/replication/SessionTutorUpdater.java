package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.SessionCourseClassTutorStub;

import java.util.List;

public class SessionTutorUpdater extends AbstractWillowUpdater<SessionCourseClassTutorStub, SessionTutor> {

	@Override
	protected void updateEntity(SessionCourseClassTutorStub stub, SessionTutor entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(college);
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setSession(updateRelationShip(stub.getSessionId(), Session.class, result));
		entity.setTutor(updateRelationShip(stub.getTutorId(), Tutor.class, result));
	}
}
