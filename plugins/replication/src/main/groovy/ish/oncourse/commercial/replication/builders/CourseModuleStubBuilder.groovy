/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.CourseModule
import ish.oncourse.webservices.v23.stubs.replication.CourseModuleStub

/**
 */
class CourseModuleStubBuilder extends AbstractAngelStubBuilder<CourseModule, CourseModuleStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected CourseModuleStub createFullStub(CourseModule entity) {
		def stub = new CourseModuleStub()
		stub.setCourseId(entity.getCourse().getId())
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setModuleId(entity.getModule().getWillowId())
		return stub
	}
}
