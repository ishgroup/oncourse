package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.v4.stubs.replication.TutorStub;

import org.apache.cayenne.Cayenne;

public class TutorUpdater extends AbstractWillowUpdater<TutorStub, Tutor> {

	@Override
	protected void updateEntity(TutorStub stub, Tutor entity, RelationShipCallback callback) {	
		
		entity.setAngelId(stub.getAngelId());
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		Long contactId = stub.getContactId();
		if (contactId != null) {
			Contact c = Cayenne.objectForPK(entity.getObjectContext(), Contact.class, contactId);
			entity.setContact(c);
		}
		
		entity.setFinishDate(stub.getFinishDate());
		entity.setStartDate(stub.getStartDate());
		entity.setResume(stub.getResume());
		entity.setResumeTextile(stub.getResumeTextile());

	}
}


