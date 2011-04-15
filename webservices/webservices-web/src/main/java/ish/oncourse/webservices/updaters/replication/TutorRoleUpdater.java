package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassTutorStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class TutorRoleUpdater extends AbstractWillowUpdater<CourseClassTutorStub, TutorRole> {

	@Override
	protected void updateEntity(CourseClassTutorStub stub, TutorRole entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(college);
		entity.setConfirmedDate(stub.getConfirmedOn());
		entity.setCourseClass(updateRelationShip(stub.getCourseClassId(), CourseClass.class, result));
		entity.setCreated(stub.getCreated());
		entity.setIsConfirmed(stub.isInPublicity());
		entity.setModified(stub.getModified());
		entity.setTutor(updateRelationShip(stub.getTutorId(), Tutor.class, result));
	}
}
