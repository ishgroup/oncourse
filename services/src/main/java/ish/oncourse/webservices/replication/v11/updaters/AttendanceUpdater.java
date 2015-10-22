package ish.oncourse.webservices.replication.v11.updaters;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Session;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.UpdaterException;
import ish.oncourse.webservices.v11.stubs.replication.AttendanceStub;

public class AttendanceUpdater extends AbstractWillowUpdater<AttendanceStub, Attendance> {

	@Override
	protected void updateEntity(AttendanceStub stub, Attendance entity, RelationShipCallback callback) {
		entity.setAttendanceType(stub.getAttendanceType());
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setSession(callback.updateRelationShip(stub.getSessionId(), Session.class));
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
		entity.setDurationMinutes(stub.getDurationMinutes());
		entity.setNote(stub.getNote());
		
		if (stub.getMarkedByTutorDate() != null) {
			entity.setMarkedByTutorDate(stub.getMarkedByTutorDate());
		}

		if (stub.getMarkedByTutorId() != null) {
			entity.setMarkedByTutor(callback.updateRelationShip(stub.getMarkedByTutorId(), Tutor.class));
		}
	}
}
