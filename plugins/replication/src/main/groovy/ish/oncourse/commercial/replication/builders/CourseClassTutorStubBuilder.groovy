/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.webservices.v23.stubs.replication.CourseClassTutorStub

/**
 */
class CourseClassTutorStubBuilder extends AbstractAngelStubBuilder<CourseClassTutor, CourseClassTutorStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected CourseClassTutorStub createFullStub(CourseClassTutor entity) {
		def stub = new CourseClassTutorStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setCourseClassId(entity.getCourseClass().getId())
		stub.setTutorId(entity.getTutor().getId())
		stub.setConfirmedOn(entity.getConfirmedOn())
		stub.setInPublicity(entity.getInPublicity())
		return stub
	}
}
