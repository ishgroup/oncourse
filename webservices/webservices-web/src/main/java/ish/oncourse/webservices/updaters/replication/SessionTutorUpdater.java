package ish.oncourse.webservices.updaters.replication;

import java.util.List;

import ish.oncourse.model.College;
import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.SessionTutorStub;

public class SessionTutorUpdater extends AbstractWillowUpdater<SessionTutorStub, SessionTutor> {
	
	public SessionTutorUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}

	@Override
	protected void updateEntity(SessionTutorStub stub, SessionTutor entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(getCollege(entity.getObjectContext()));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setSession((Session) updateRelatedEntity(entity.getObjectContext(), stub.getSession(), relationStubs));
		entity.setTutor((Tutor) updateRelatedEntity(entity.getObjectContext(), stub.getTutor(), relationStubs));
	}
}
