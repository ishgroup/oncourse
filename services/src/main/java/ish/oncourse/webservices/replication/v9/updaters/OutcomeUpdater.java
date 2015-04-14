package ish.oncourse.webservices.replication.v9.updaters;

import ish.common.types.OutcomeStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Module;
import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v9.stubs.replication.OutcomeStub;
import org.apache.cayenne.Cayenne;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OutcomeUpdater extends AbstractWillowUpdater<OutcomeStub, Outcome> {
	private static final Logger logger = LogManager.getLogger();

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
			logger.error("Can not find enrolment by angelId: {}", stub.getEnrolmentId());
		}
		entity.setFundingSource(stub.getFundingSource());
		if (stub.getModuleId() != null) {
			entity.setModule(Cayenne.objectForPK(entity.getObjectContext(), Module.class, stub.getModuleId()));
		}
		if (stub.getReportableHours() != null) {
			entity.setReportableHours(stub.getReportableHours().doubleValue());
		}
		if (stub.getStatus() != null) {
			entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), OutcomeStatus.class));
		} else {
			entity.setStatus(OutcomeStatus.STATUS_NOT_SET);
		}
	}

}
