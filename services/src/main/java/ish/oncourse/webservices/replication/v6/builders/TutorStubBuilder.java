package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.TutorStub;

public class TutorStubBuilder extends AbstractWillowStubBuilder<Tutor, TutorStub> {

	@Override
	protected TutorStub createFullStub(Tutor entity) {
		TutorStub stub = new TutorStub();
		Contact contact = entity.getContact();
		if (contact != null) {
			stub.setContactId(contact.getId());
		}
		stub.setCreated(entity.getCreated());
		stub.setFinishDate(entity.getFinishDate());
		stub.setModified(entity.getModified());
		stub.setResumeTextile(entity.getResumeTextile());
		stub.setStartDate(entity.getStartDate());
		return stub;
	}
}
