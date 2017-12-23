package ish.oncourse.webservices.replication.v17.builders;

import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v17.stubs.replication.OutcomeStub;

import java.math.BigDecimal;

public class OutcomeStubBuilder extends AbstractWillowStubBuilder<Outcome, OutcomeStub> {
	
	@Override
	protected OutcomeStub createFullStub(Outcome entity) {
		OutcomeStub stub = new OutcomeStub();
		stub.setCreated(entity.getCreated());
		if (entity.getDeliveryMode() != null) {
			stub.setDeliveryMode(entity.getDeliveryMode());
		}
		
		if (entity.getEnrolment() != null) {
			stub.setEnrolmentId(entity.getEnrolment().getId());
		} else if (entity.getPriorLearning() != null) {
			stub.setPriorLearningId(entity.getPriorLearning().getId());
		}
		
		if (entity.getFundingSource() != null) {
			stub.setFundingSource(entity.getFundingSource().getDatabaseValue());
		}
		stub.setModified(entity.getModified());
		if (entity.getModule() != null) {
			stub.setModuleId(entity.getModule().getId());
		}
		if (entity.getStatus() != null) {
			stub.setStatus(entity.getStatus().getDatabaseValue());
		}
		if (entity.getReportableHours() != null) {
			stub.setReportableHours(new BigDecimal(entity.getReportableHours()));
		}

		stub.setMarkedByTutorDate(entity.getMarkedByTutorDate());
		
		if (entity.getMarkedByTutor() != null) {
			stub.setMarkedByTutorId(entity.getMarkedByTutor().getId());
		}
		
		stub.setEndDate(entity.getEndDate());
		stub.setStartDate(entity.getStartDate());
		return stub;
	}

}
