package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.v4.stubs.replication.TutorStub;

public class TutorUpdater extends AbstractWillowUpdater<TutorStub, Tutor> {

	@Override
	protected void updateEntity(TutorStub stub, Tutor entity, RelationShipCallback callback) {	
	
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		entity.setFinishDate(stub.getFinishDate());
		entity.setStartDate(stub.getStartDate());
		entity.setResume(stub.getResume());
		entity.setResumeTextile(stub.getResumeTextile());

	}
}


