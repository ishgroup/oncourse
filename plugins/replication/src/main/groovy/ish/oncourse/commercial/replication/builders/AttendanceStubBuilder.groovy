/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.webservices.v23.stubs.replication.AttendanceStub

@CompileStatic
class AttendanceStubBuilder extends AbstractAngelStubBuilder<Attendance, AttendanceStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected AttendanceStub createFullStub(Attendance entity) {
		def stub = new AttendanceStub()
		stub.setAttendanceType(entity.getAttendanceType().getDatabaseValue())
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setSessionId(entity.getSession().getId())
		stub.setStudentId(entity.getStudent().getId())
		def marker = entity.getMarkedByTutor()
		if (marker != null) {
			stub.setMarkedByTutorId(marker.getId())
		}
		entity.setMarkedByTutorDate(entity.getMarkedByTutorDate())
		stub.setDurationMinutes(entity.getDurationMinutes())
		stub.setNote(entity.getNote())
		stub.setAttendedFrom(entity.getAttendedFrom())
		stub.setAttendedUntil(entity.getAttendedUntil())
		return stub
	}
}
