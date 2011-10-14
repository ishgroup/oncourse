package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.v4.stubs.replication.TutorStub;

public class TutorStubBuilder extends AbstractWillowStubBuilder<Tutor, TutorStub> {

	@Override
	protected TutorStub createFullStub(Tutor entity) {
		TutorStub stub = new TutorStub();
		
		stub.setContactId(entity.getContact().getId());
		stub.setCreated(entity.getCreated());
		stub.setFinishDate(entity.getFinishDate());
		stub.setModified(entity.getModified());
		stub.setResumeTextile(entity.getResumeTextile());
		stub.setStartDate(entity.getStartDate());
		
		return stub;
	}
}
