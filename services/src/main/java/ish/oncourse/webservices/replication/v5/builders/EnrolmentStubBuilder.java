package ish.oncourse.webservices.replication.v5.builders;

import ish.oncourse.model.Enrolment;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v5.stubs.replication.EnrolmentStub;

public class EnrolmentStubBuilder extends AbstractWillowStubBuilder<Enrolment, EnrolmentStub> {
	
	@Override
	protected EnrolmentStub createFullStub(Enrolment entity) {
		EnrolmentStub stub = new EnrolmentStub();
		
		stub.setCourseClassId(entity.getCourseClass().getId());
		stub.setCreated(entity.getCreated());
		//pass of the enrollment invoice line is redundant for angel >=4.1 because updaters will ignore it in. Need to have backward compatibility
		if (entity.getInvoiceLines() != null && !entity.getInvoiceLines().isEmpty() && entity.getOriginalInvoiceLine() != null) {
			stub.setInvoiceLineId(entity.getOriginalInvoiceLine().getId());
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
