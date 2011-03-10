package ish.oncourse.webservices.updaters.replication;

import java.util.List;

import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.TutorRoleStub;

public class TutorRoleUpdater extends AbstractWillowUpdater<TutorRoleStub, TutorRole> {
	
	public TutorRoleUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}

	@Override
	protected void updateEntity(TutorRoleStub stub, TutorRole entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(getCollege(entity.getObjectContext()));
		entity.setConfirmedDate(stub.getConfirmedDate());
		entity.setCourseClass((CourseClass) updateRelatedEntity(entity.getObjectContext(), stub.getCourseClass(), relationStubs));
		entity.setCreated(stub.getCreated());
		entity.setDetail(stub.getDetail());
		entity.setIsConfirmed(stub.isConfirmed());
		entity.setModified(stub.getModified());
		entity.setTutor((Tutor) updateRelatedEntity(entity.getObjectContext(), stub.getTutor(), relationStubs));
		
	}
}
