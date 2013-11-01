package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.CourseClassTutorStub;

public class TutorRoleUpdater extends AbstractWillowUpdater<CourseClassTutorStub, TutorRole> {

	@Override
	protected void updateEntity(CourseClassTutorStub stub, TutorRole entity, RelationShipCallback callback) {
		entity.setConfirmedDate(stub.getConfirmedOn());
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class));
		entity.setCreated(stub.getCreated());
		entity.setIsConfirmed(stub.getConfirmedOn() != null);
        entity.setConfirmedDate(stub.getConfirmedOn());
		entity.setModified(stub.getModified());
		entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class));
		entity.setInPublicity(Boolean.TRUE.equals(stub.isInPublicity()));
	}
}
