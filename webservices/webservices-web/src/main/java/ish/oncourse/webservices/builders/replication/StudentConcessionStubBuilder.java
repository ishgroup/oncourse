package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.webservices.v4.stubs.replication.StudentConcessionStub;

import java.util.Map;

public class StudentConcessionStubBuilder extends AbstractWillowStubBuilder<StudentConcession, StudentConcessionStub> { 
	
	public StudentConcessionStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowStubBuilder next) {
		super(queue, next);
	}
	
	@Override
	protected StudentConcessionStub createFullStub(StudentConcession entity) {
		StudentConcessionStub stub = new StudentConcessionStub();
		
		stub.setWillowId(entity.getId());
		stub.setAuthorisationExpiresOn(entity.getAuthorisationExpiresOn());
		stub.setAuthorisedOn(entity.getAuthorisedOn());
		stub.setConcessionNumber(entity.getConcessionNumber());
		stub.setConcessionType(findRelatedStub(entity.getConcessionType()));
		stub.setCreated(entity.getCreated());
		stub.setExpiresOn(entity.getExpiresOn());
		stub.setModified(entity.getModified());
		stub.setStudent(findRelatedStub(entity.getStudent()));
		stub.setTimeZone(entity.getTimeZone());
		
		return stub;
	}
}
