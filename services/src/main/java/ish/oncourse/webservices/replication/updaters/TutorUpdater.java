package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Tutor;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.v4.stubs.replication.TutorStub;

public class TutorUpdater extends AbstractWillowUpdater<TutorStub, Tutor> {

	private ITextileConverter textileConverter;

	public TutorUpdater(ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(TutorStub stub, Tutor entity, RelationShipCallback callback) {

		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());

		entity.setFinishDate(stub.getFinishDate());
		entity.setStartDate(stub.getStartDate());
		String resumeTextile = stub.getResumeTextile();
		if (resumeTextile != null) {
			entity.setResume(textileConverter.convertCoreTextile(resumeTextile));
		}
		entity.setResumeTextile(resumeTextile);

	}
}
