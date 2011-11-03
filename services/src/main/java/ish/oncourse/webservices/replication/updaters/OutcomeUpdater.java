package ish.oncourse.webservices.replication.updaters;

import java.math.BigDecimal;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.v4.stubs.replication.OutcomeStub;

public class OutcomeUpdater extends AbstractWillowUpdater<OutcomeStub, Outcome> {

	@Override
	protected void updateEntity(OutcomeStub stub, Outcome entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setDeliveryMode(stub.getDeliveryMode());
		entity.setStartDate(stub.getStartDate());
		entity.setEndDate(stub.getEndDate());
		
		Enrolment enrolment = callback.updateRelationShip(stub.getEnrolmentId(), Enrolment.class);
		entity.setEnrolment(enrolment);
		
		entity.setFundingSource(stub.getFundingSource());
		entity.setModuleId(stub.getModuleId());
		BigDecimal reportableHours = stub.getReportableHours();
		if (reportableHours != null) {
			entity.setReportableHours(reportableHours.doubleValue());
		}
		entity.setStatus(stub.getStatus());
	}

}
