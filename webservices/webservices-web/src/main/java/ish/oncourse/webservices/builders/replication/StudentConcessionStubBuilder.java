package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.StudentConcessionStub;

import java.util.Map;

public class StudentConcessionStubBuilder extends AbstractWillowStubBuilder<StudentConcession, StudentConcessionStub> { 
	
	public StudentConcessionStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowQueueService queueService, IWillowStubBuilder next) {
		super(queue, queueService, next);
	}
	
	@Override
	protected StudentConcessionStub createFullStub(StudentConcession entity) {
		StudentConcessionStub stub = new StudentConcessionStub();
		
		stub.setWillowId(entity.getId());
		stub.setAuthorisationExpiresOn(entity.getAuthorisationExpiresOn());
		stub.setAuthorisedOn(entity.getAuthorisedOn());
		stub.setConcessionNumber(entity.getConcessionNumber());
		stub.setConcessionType(findRelationshipStub(entity.getConcessionType()));
		stub.setCreated(entity.getCreated());
		stub.setExpiresOn(entity.getExpiresOn());
		stub.setModified(entity.getModified());
		stub.setStudent(findRelationshipStub(entity.getStudent()));
		stub.setTimeZone(entity.getTimeZone());
		
		return stub;
	}
}
