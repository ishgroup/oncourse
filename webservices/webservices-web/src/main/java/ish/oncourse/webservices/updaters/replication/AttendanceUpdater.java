package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Session;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.v4.stubs.replication.AttendanceStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class AttendanceUpdater extends AbstractWillowUpdater<AttendanceStub, Attendance> {

	@Override
	protected void updateEntity(AttendanceStub stub, Attendance entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setAttendanceType(stub.getAttendanceType());
		entity.setCollege(college);
		entity.setCreated(stub.getCreated());
		entity.setMarker(updateRelationShip(stub.getMarkerId(), Tutor.class, result));
		entity.setModified(stub.getModified());
		entity.setSession(updateRelationShip(stub.getSessionId(), Session.class, result));
		entity.setStudent(updateRelationShip(stub.getStudentId(), Student.class, result));
	}
}
