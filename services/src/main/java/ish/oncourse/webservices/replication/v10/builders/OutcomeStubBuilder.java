package ish.oncourse.webservices.replication.v10.builders;

import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v10.stubs.replication.OutcomeStub;

import java.math.BigDecimal;

public class OutcomeStubBuilder extends AbstractWillowStubBuilder<Outcome, OutcomeStub> {
	
	@Override
	protected OutcomeStub createFullStub(Outcome entity) {
		OutcomeStub stub = new OutcomeStub();
		stub.setCreated(entity.getCreated());
		if (entity.getDeliveryMode() != null) {
			stub.setDeliveryMode(entity.getDeliveryMode());
		}
		stub.setEnrolmentId(entity.getEnrolment().getId());
		if (entity.getFundingSource() != null) {
			stub.setFundingSource(entity.getFundingSource());
		}
		stub.setModified(entity.getModified());
		if (entity.getModule() != null) {
			stub.setModuleId(entity.getModule().getId());
		}
		if (entity.getStatus() != null) {
			stub.setStatus(entity.getStatus().getDatabaseValue());
		}

		stub.setEndDate(entity.getEndDate());		
		if (entity.getReportableHours() != null) {
			stub.setReportableHours(new BigDecimal(entity.getReportableHours()));
		}
		return stub;
	}

}
