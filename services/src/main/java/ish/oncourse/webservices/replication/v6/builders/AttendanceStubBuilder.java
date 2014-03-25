package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Contact;
import ish.oncourse.util.CommonUtils;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.AttendanceStub;

import javax.xml.bind.JAXBElement;

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
		if (entity.getMarker() != null) {
			Contact marker = entity.getMarker().getContact();
			if (marker != null) {
				stub.setMarkerId(marker.getId());
			}
		}
		/**
		 * The code needs until while our system has colleges with version less or equal 5.0rc8
		 */
		if (CommonUtils.compare(entity.getCollege().getAngelVersion(), "5.0rc8") > 0)
		{
				stub.setDurationMinutes(entity.getDurationMinutes());
				stub.setNote(entity.getNote());
		}
		return stub;
	}
}
