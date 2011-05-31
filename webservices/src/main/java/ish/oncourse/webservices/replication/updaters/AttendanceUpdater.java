package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Session;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.v4.stubs.replication.AttendanceStub;

public class AttendanceUpdater extends AbstractWillowUpdater<AttendanceStub, Attendance> {

	@Override
	protected void updateEntity(AttendanceStub stub, Attendance entity, RelationShipCallback callback) {
		entity.setAttendanceType(stub.getAttendanceType());
		entity.setCreated(stub.getCreated());
		entity.setMarker(callback.updateRelationShip(stub.getMarkerId(), Tutor.class));
		entity.setModified(stub.getModified());
		entity.setSession(callback.updateRelationShip(stub.getSessionId(), Session.class));
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
	}
}
