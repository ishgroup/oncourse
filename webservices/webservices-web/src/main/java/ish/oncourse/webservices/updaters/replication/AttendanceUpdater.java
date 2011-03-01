package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.College;
import ish.oncourse.model.Session;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.v4.stubs.replication.AttendanceStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;

import java.util.ArrayList;
import java.util.List;

public class AttendanceUpdater extends AbstractWillowUpdater<AttendanceStub, Attendance> {
	
	public AttendanceUpdater(College college, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, next);
	}
	
	@Override
	protected List<HollowStub> updateEntity(AttendanceStub stub, Attendance entity) {
		
		List<HollowStub> relationStubs = new ArrayList<HollowStub>();
		
		entity.setAngelId(stub.getAngelId());
		entity.setAttendanceType(stub.getAttendanceType());
		
		entity.setCollege(college);
		
		entity.setCreated(stub.getCreated());
		entity.setMarker((Tutor) updateRelatedEntity(relationStubs, stub.getMarker()));
		entity.setModified(stub.getModified());
		entity.setSession((Session) updateRelatedEntity(relationStubs, stub.getSession()));
		entity.setStudent((Student) updateRelatedEntity(relationStubs, stub.getStudent()));
		
		return relationStubs;
	}
	
}
