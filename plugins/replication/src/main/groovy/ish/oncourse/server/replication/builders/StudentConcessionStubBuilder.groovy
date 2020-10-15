/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.StudentConcession
import ish.oncourse.webservices.v21.stubs.replication.StudentConcessionStub

/**
 */
class StudentConcessionStubBuilder extends AbstractAngelStubBuilder<StudentConcession, StudentConcessionStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected StudentConcessionStub createFullStub(StudentConcession s) {
		def stub = new StudentConcessionStub()
		stub.setAuthorisationExpiresOn(s.getAuthorisationExpiresOn())
		stub.setAuthorisedOn(s.getAuthorisedOn())
		stub.setConcessionNumber(s.getConcessionNumber())
		stub.setCreated(s.getCreatedOn())
		stub.setExpiresOn(s.getExpiresOn())
		stub.setModified(s.getModifiedOn())
		stub.setConcessionTypeId(s.getConcessionType().getId())
		stub.setStudentId(s.getStudent().getId())
		return stub
	}
}
