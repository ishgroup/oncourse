/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.common.types.AttendanceType
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.webservices.v23.stubs.replication.AttendanceStub

@CompileStatic
class AttendanceUpdater extends AbstractAngelUpdater<AttendanceStub, Attendance> {


	@Override
	protected void updateEntity(AttendanceStub stub, Attendance entity, RelationShipCallback callback) {
		def type = TypesUtil.getEnumForDatabaseValue(stub.getAttendanceType(), AttendanceType.class)
		if (type != null) {
			entity.setAttendanceType(type)
		} else {
			entity.setAttendanceType(AttendanceType.UNMARKED)
		}
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setSession(callback.updateRelationShip(stub.getSessionId(), Session.class))
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class))
		entity.setDurationMinutes(stub.getDurationMinutes())
		entity.setNote(stub.getNote())
		entity.setMarkedByTutorDate(stub.getMarkedByTutorDate())
		if (stub.getMarkedByTutorId() != null) {
			entity.setMarkedByTutor(callback.updateRelationShip(stub.getMarkedByTutorId(), Tutor.class))
		}
		entity.setAttendedFrom(stub.getAttendedFrom())
		entity.setAttendedUntil(stub.getAttendedUntil())
	}
}
