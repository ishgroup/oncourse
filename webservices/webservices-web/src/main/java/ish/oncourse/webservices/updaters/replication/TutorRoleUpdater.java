package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.TutorRoleStub;

import java.util.List;

public class TutorRoleUpdater extends AbstractWillowUpdater<TutorRoleStub, TutorRole> {

	@SuppressWarnings("unchecked")
	@Override
	protected void updateEntity(TutorRoleStub stub, TutorRole entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(college);
		entity.setConfirmedDate(stub.getConfirmedDate());
		entity.setCourseClass((CourseClass) updateRelationShip(stub.getCourseClassId(), "CourseClass", result));
		entity.setCreated(stub.getCreated());
		entity.setDetail(stub.getDetail());
		entity.setIsConfirmed(stub.isConfirmed());
		entity.setModified(stub.getModified());
		entity.setTutor((Tutor) updateRelationShip(stub.getTutorId(), "Tutor", result));
	}
}
