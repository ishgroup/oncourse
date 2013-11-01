package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.OutcomeStub;

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
		if (entity.getModuleId() != null) {
			stub.setModuleId(entity.getModuleId());
		}
		stub.setReportableHours(new BigDecimal(entity.getReportableHours()));
		return stub;
	}

}
