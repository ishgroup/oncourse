package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Enrolment;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;

public class EnrolmentStubBuilder extends AbstractWillowStubBuilder<Enrolment, EnrolmentStub> {
	
	@Override
	protected EnrolmentStub createFullStub(Enrolment entity) {
		EnrolmentStub stub = new EnrolmentStub();
		
		stub.setCourseClassId(entity.getCourseClass().getId());
		stub.setCreated(entity.getCreated());
		if (entity.getInvoiceLine() != null) {
			stub.setInvoiceLineId(entity.getInvoiceLine().getId());
		} else {
			logger.error("Enrollment without linked invoice line found for college" + entity.getCollege().getId() + " with willowid = " + entity.getId() 
				+ " and angelid=" + entity.getAngelId());
		}
		stub.setModified(entity.getModified());
		
		if (entity.getReasonForStudy() != null) {
			stub.setReasonForStudy(entity.getReasonForStudy());
		}
		else {
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
