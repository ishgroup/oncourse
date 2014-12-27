package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Session;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.UpdaterException;
import ish.oncourse.webservices.v7.stubs.replication.AttendanceStub;

public class AttendanceUpdater extends AbstractWillowUpdater<AttendanceStub, Attendance> {

	@Override
	protected void updateEntity(AttendanceStub stub, Attendance entity, RelationShipCallback callback) {
		entity.setAttendanceType(stub.getAttendanceType());
		entity.setCreated(stub.getCreated());
		Long markerId = stub.getMarkerId();
		if (markerId != null) {
			Contact contact = callback.updateRelationShip(markerId, Contact.class);
            if (contact == null) {
                throw new UpdaterException(String.format("Contact cannot be null for Attendance angelId: %s and markerId: %s" , stub.getAngelId(), markerId));
            }
			if (contact.getTutor() != null) {
				entity.setMarker(contact.getTutor());
			}
		}
		entity.setModified(stub.getModified());
		entity.setSession(callback.updateRelationShip(stub.getSessionId(), Session.class));
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
		entity.setDurationMinutes(stub.getDurationMinutes());
		entity.setNote(stub.getNote());
	}
}
