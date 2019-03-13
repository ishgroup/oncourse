package ish.oncourse.webservices.replication.v20.builders;

import ish.oncourse.model.TutorRole;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v20.stubs.replication.CourseClassTutorStub;

public class TutorRoleStubBuilder extends AbstractWillowStubBuilder<TutorRole, CourseClassTutorStub> {

	@Override
	protected CourseClassTutorStub createFullStub(TutorRole entity) {
		CourseClassTutorStub stub = new CourseClassTutorStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setCourseClassId(entity.getCourseClass().getId());
		stub.setTutorId(entity.getTutor().getId());
		stub.setConfirmedOn(entity.getConfirmedDate());
		stub.setInPublicity(entity.getInPublicity());
		return stub;
	}
	
}
