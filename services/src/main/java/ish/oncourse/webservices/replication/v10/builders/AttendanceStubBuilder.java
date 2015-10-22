package ish.oncourse.webservices.replication.v10.builders;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Contact;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v10.stubs.replication.AttendanceStub;

public class AttendanceStubBuilder extends AbstractWillowStubBuilder<Attendance, AttendanceStub>{

	@Override
	protected AttendanceStub createFullStub(Attendance entity) {
		AttendanceStub stub = new AttendanceStub();
		if (entity.getAttendanceType() != null) {
			stub.setAttendanceType(entity.getAttendanceType());
		} else {
			//unmarked
			stub.setAttendanceType(0);
		}
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setSessionId(entity.getSession().getId());
		stub.setStudentId(entity.getStudent().getId());
		stub.setDurationMinutes(entity.getDurationMinutes());
		stub.setNote(entity.getNote());
		
		return stub;
	}
}
