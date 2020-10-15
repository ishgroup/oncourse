/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.CorporatePassCourseClass
import ish.oncourse.webservices.v21.stubs.replication.CorporatePassCourseClassStub

/**
 */
class CorporatePassCourseClassStubBuilder extends AbstractAngelStubBuilder<CorporatePassCourseClass, CorporatePassCourseClassStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected CorporatePassCourseClassStub createFullStub(CorporatePassCourseClass entity) {
		def stub = new CorporatePassCourseClassStub()

		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setCorporatePassId(entity.getCorporatePass().getId())
		stub.setCourseClassId(entity.getCourseClass().getId())

		return stub
	}

}
