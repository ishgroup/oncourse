package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.TutorRole;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassTutorStub;

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

		return stub;
	}
	
}
