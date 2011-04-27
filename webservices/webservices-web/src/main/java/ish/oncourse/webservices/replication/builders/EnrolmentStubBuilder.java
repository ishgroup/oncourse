package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Enrolment;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;

public class EnrolmentStubBuilder extends AbstractWillowStubBuilder<Enrolment, EnrolmentStub> {
	
	@Override
	protected EnrolmentStub createFullStub(Enrolment entity) {
		EnrolmentStub stub = new EnrolmentStub();
		
		stub.setCourseClassId(entity.getCourseClass().getId());
		stub.setCreated(entity.getCreated());
		stub.setInvoiceLineId(entity.getInvoiceLine().getId());
		stub.setModified(entity.getModified());
		stub.setReasonForStudy(entity.getReasonForStudy());
		
		if (entity.getSource() != null) {
			stub.setSource(entity.getSource().name());
		}
		
		if (entity.getStatus() != null) {
			stub.setStatus(entity.getStatus().name());
		}
		
		stub.setStudentId(entity.getStudent().getId());
		
		return stub;
	}
}
