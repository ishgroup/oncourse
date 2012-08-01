package ish.oncourse.webservices.replication.v5.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tutor;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.TutorStub;

public class TutorUpdater extends AbstractWillowUpdater<TutorStub, Tutor> {

	private ITextileConverter textileConverter;

	public TutorUpdater(ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(TutorStub stub, Tutor entity, RelationShipCallback callback) {

		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		if (stub.getContactId() != null) {
			Contact contact = callback.updateRelationShip(stub.getContactId(), Contact.class);
			contact.setTutor(entity);
		}

		entity.setFinishDate(stub.getFinishDate());
		entity.setStartDate(stub.getStartDate());
		String resumeTextile = stub.getResumeTextile();
		if (resumeTextile != null) {
			entity.setResume(textileConverter.convertCoreTextile(resumeTextile));
		}
		entity.setResumeTextile(resumeTextile);

	}
}
