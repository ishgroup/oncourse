package ish.oncourse.webservices.replication.v7.builders;

import ish.oncourse.model.StudentConcession;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v7.stubs.replication.StudentConcessionStub;

public class StudentConcessionStubBuilder extends AbstractWillowStubBuilder<StudentConcession, StudentConcessionStub> { 
	
	@Override
	protected StudentConcessionStub createFullStub(StudentConcession entity) {
		StudentConcessionStub stub = new StudentConcessionStub();
		stub.setAuthorisationExpiresOn(entity.getAuthorisationExpiresOn());
		stub.setAuthorisedOn(entity.getAuthorisedOn());
		stub.setConcessionNumber(entity.getConcessionNumber());
		stub.setConcessionTypeId(entity.getConcessionType().getId());
		stub.setCreated(entity.getCreated());
		stub.setExpiresOn(entity.getExpiresOn());
		stub.setModified(entity.getModified());
		stub.setStudentId(entity.getStudent().getId());
		stub.setTimeZone(entity.getTimeZone());
		return stub;
	}
}
