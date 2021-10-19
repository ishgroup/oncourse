package ish.oncourse.webservices.replication.v24.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tutor;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.UpdaterException;
import ish.oncourse.webservices.v24.stubs.replication.TutorStub;

public class TutorUpdater extends AbstractWillowUpdater<TutorStub, Tutor> {

	private IRichtextConverter textileConverter;

	public TutorUpdater(IRichtextConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(TutorStub stub, Tutor entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		if (stub.getContactId() != null) {
			entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		} else {
			final String message = String.format("Tutor with angelId = %s without linked contact detected!", stub.getAngelId());
            throw new UpdaterException(message);
		}
		entity.setFinishDate(stub.getFinishDate());
		entity.setStartDate(stub.getStartDate());
		String resumeTextile = stub.getResumeTextile();
		if (resumeTextile != null) {
			entity.setResume(textileConverter.convertCoreText(resumeTextile));
		}
		entity.setResumeTextile(resumeTextile);

	}
}
