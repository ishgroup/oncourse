package ish.oncourse.webservices.replication.v5.builders;

import ish.oncourse.model.TutorRole;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v5.stubs.replication.CourseClassTutorStub;

public class TutorRoleStubBuilder extends AbstractWillowStubBuilder<TutorRole, CourseClassTutorStub> {

	/* (non-Javadoc)
	 * @see ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder#createFullStub(ish.oncourse.model.Queueable)
	 */
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
