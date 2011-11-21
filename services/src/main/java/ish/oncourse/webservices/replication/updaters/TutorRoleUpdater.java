package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassTutorStub;

public class TutorRoleUpdater extends AbstractWillowUpdater<CourseClassTutorStub, TutorRole> {

	@Override
	protected void updateEntity(CourseClassTutorStub stub, TutorRole entity, RelationShipCallback callback) {
		
		entity.setConfirmedDate(stub.getConfirmedOn());
		
		CourseClass courseClass = callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class);
		entity.setCourseClass(courseClass);
		
		entity.setCreated(stub.getCreated());
		entity.setIsConfirmed(stub.getConfirmedOn() != null);
		entity.setModified(stub.getModified());
		entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class));
	}
}
