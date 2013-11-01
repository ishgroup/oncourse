package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.Enrolment;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.EnrolmentStub;

public class EnrolmentStubBuilder extends AbstractWillowStubBuilder<Enrolment, EnrolmentStub> {
	
	@Override
	protected EnrolmentStub createFullStub(Enrolment entity) {
		EnrolmentStub stub = new EnrolmentStub();
		stub.setCourseClassId(entity.getCourseClass().getId());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		if (entity.getReasonForStudy() != null) {
			stub.setReasonForStudy(entity.getReasonForStudy());
		} else {
			stub.setReasonForStudy(-1);
		}
		stub.setSource(entity.getSource().getDatabaseValue());
		if (entity.getStatus() != null) {
			stub.setStatus(entity.getStatus().name());
		}
		stub.setStudentId(entity.getStudent().getId());
		return stub;
	}
}
