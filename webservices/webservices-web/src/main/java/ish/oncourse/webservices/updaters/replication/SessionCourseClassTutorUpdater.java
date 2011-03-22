package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.webservices.v4.stubs.replication.CourseClassTutorStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.SessionCourseClassTutorStub;
import ish.oncourse.webservices.v4.stubs.replication.SessionTutorStub;
import ish.oncourse.webservices.v4.stubs.replication.TutorRoleStub;

import java.util.ArrayList;
import java.util.List;

public class SessionCourseClassTutorUpdater implements IWillowUpdater<SessionCourseClassTutorStub> {
	
	private @SuppressWarnings("rawtypes") IWillowUpdater next;
	
	public SessionCourseClassTutorUpdater(@SuppressWarnings("rawtypes") IWillowUpdater next) {
		this.next = next;
	}
	
	@Override
	public List<ReplicatedRecord> updateRecord(SessionCourseClassTutorStub stub) {
		List<ReplicatedRecord> records = new ArrayList<ReplicatedRecord>();
		
		CourseClassTutorStub courseClassTutorStub = (CourseClassTutorStub) stub.getCourseClassTutor();
		
		ReplicationStub sessionStub = stub.getSession();
		ReplicationStub courseClassStub = courseClassTutorStub.getCourseClass();
		ReplicationStub tutorStub = courseClassTutorStub.getTutor();
		
		TutorRoleStub tutorRoleStub = new TutorRoleStub();
		tutorRoleStub.setAngelId(courseClassTutorStub.getAngelId());
		tutorRoleStub.setEntityIdentifier("TutorRole");
		tutorRoleStub.setCourseClass(courseClassStub);
		tutorRoleStub.setTutor(tutorStub);
		
		List<ReplicatedRecord> confirmedRQ = next.updateRecord(tutorRoleStub);
		
		SessionTutorStub sessionTutorStub = new SessionTutorStub();
		sessionTutorStub.setEntityIdentifier("SessionTutor");
		sessionTutorStub.setSession(sessionStub);
		sessionTutorStub.setTutor(tutorStub);
		
		confirmedRQ = next.updateRecord(sessionTutorStub);
		
		return records;
	}
}
