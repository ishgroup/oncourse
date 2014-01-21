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

		stub.setCreditOfferedValue(entity.getCreditOfferedValue());
		stub.setCreditProvider(entity.getCreditProvider());
		stub.setCreditUsedValue(entity.getCreditUsedValue());
		if (entity.getCreditType() != null) {
			stub.setCreditType(entity.getCreditType().getDatabaseValue());
		}
		stub.setCreditFoeId(entity.getCreditFOEId());
		if (entity.getCreditLevel() != null) {
			stub.setCreditLevel(entity.getCreditLevel().getDatabaseValue());
		}
		if (entity.getCreditProviderType() != null) {
			stub.setCreditProviderType(entity.getCreditProviderType().getDatabaseValue());
		}
		if (entity.getFeeStatus() != null) {
			stub.setFeeStatus(entity.getFeeStatus().getDatabaseValue());
		}
		if (entity.getCreditTotal() != null) {
			stub.setCreditTotal(entity.getCreditTotal().getDatabaseValue());
		}
		if (entity.getFeeHelpStatus() != null) {
			stub.setFeeHelpStatus(entity.getFeeHelpStatus().getDatabaseValue());
		}
		return stub;
	}
}
