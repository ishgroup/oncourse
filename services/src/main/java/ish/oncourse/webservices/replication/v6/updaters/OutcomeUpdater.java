package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.OutcomeStub;
import org.apache.log4j.Logger;

public class OutcomeUpdater extends AbstractWillowUpdater<OutcomeStub, Outcome> {
	private static final Logger logger = Logger.getLogger(OutcomeUpdater.class);

	@Override
	protected void updateEntity(OutcomeStub stub, Outcome entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setDeliveryMode(stub.getDeliveryMode());
		entity.setStartDate(stub.getStartDate());
		entity.setEndDate(stub.getEndDate());
		Enrolment enrolment = callback.updateRelationShip(stub.getEnrolmentId(), Enrolment.class);
		if (enrolment != null) {
			entity.setEnrolment(enrolment);
		} else {
			logger.error(String.format("Can not find enrolment by angelId:%s", stub.getEnrolmentId()));
		}
		entity.setFundingSource(stub.getFundingSource());
		entity.setModuleId(stub.getModuleId());
		if (stub.getReportableHours() != null) {
			entity.setReportableHours(stub.getReportableHours().doubleValue());
		}
		entity.setStatus(stub.getStatus());
	}

}
