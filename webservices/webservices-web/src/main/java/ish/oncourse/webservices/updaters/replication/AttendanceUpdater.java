package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.College;
import ish.oncourse.model.Session;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.AttendanceStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class AttendanceUpdater extends AbstractWillowUpdater<AttendanceStub, Attendance> {
	
	public AttendanceUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}
	
	@Override
	protected void updateEntity(AttendanceStub stub, Attendance entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setAttendanceType(stub.getAttendanceType());
		
		entity.setCollege(college);
		
		entity.setCreated(stub.getCreated());
		entity.setMarker((Tutor) updateRelatedEntity(stub.getMarker(), relationStubs));
		entity.setModified(stub.getModified());
		entity.setSession((Session) updateRelatedEntity(stub.getSession(), relationStubs));
		entity.setStudent((Student) updateRelatedEntity(stub.getStudent(), relationStubs));
	}
	
}
