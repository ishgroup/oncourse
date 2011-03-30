package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.SessionCourseClassTutorStub;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.util.List;

import org.apache.cayenne.ObjectContext;

public class SessionCourseClassTutorUpdater implements IWillowUpdater<SessionCourseClassTutorStub> {
	
	private ObjectContext ctx;

	private TransactionGroup group;
	private @SuppressWarnings("rawtypes") IWillowUpdater next;
	private College college;
	
	public SessionCourseClassTutorUpdater(ObjectContext ctx, College college, TransactionGroup group, IWillowUpdater next) {
		super();
		this.ctx = ctx;
		this.college = college;
		this.group = group;
		this.next = next;
	}
	
	@Override
	public Queueable updateRecord(SessionCourseClassTutorStub stub, List<ReplicatedRecord> result) {
		
		return null;
		/*
		CourseClassTutorStub courseClassTutorStub = (CourseClassTutorStub) stub.getCourseClassTutor();
		
		ReplicationStub sessionStub = stub.getSession();
		ReplicationStub courseClassStub = courseClassTutorStub.getCourseClass();
		ReplicationStub tutorStub = courseClassTutorStub.getTutor();
		
		TutorRoleStub tutorRoleStub = new TutorRoleStub();
		tutorRoleStub.setAngelId(courseClassTutorStub.getAngelId());
		tutorRoleStub.setEntityIdentifier("TutorRole");
		tutorRoleStub.setCourseClass(courseClassStub);
		tutorRoleStub.setTutor(tutorStub);
		
		next.updateRecord(tutorRoleStub, result);
		
		SessionTutorStub sessionTutorStub = new SessionTutorStub();
		sessionTutorStub.setEntityIdentifier("SessionTutor");
		sessionTutorStub.setSession(sessionStub);
		sessionTutorStub.setTutor(tutorStub);
		
		next.updateRecord(sessionTutorStub, result);*/
	}
}
