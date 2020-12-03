/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.TutorAttendance
import ish.oncourse.webservices.v23.stubs.replication.TutorAttendanceStub

/**
 */
class TutorAttendanceStubBuilder extends AbstractAngelStubBuilder<TutorAttendance, TutorAttendanceStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected TutorAttendanceStub createFullStub(TutorAttendance entity) {
		def stub = new TutorAttendanceStub()
		stub.setTutorId(entity.getCourseClassTutor().getTutor().getId())
		stub.setSessionId(entity.getSession().getId())
		stub.setDurationMinutes(entity.getDurationMinutes())
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		if (entity.getAttendanceType() != null) {
			stub.setAttendanceType(entity.getAttendanceType().getDatabaseValue())
		}
		stub.setNote(entity.getNote())
		return stub
	}
}
