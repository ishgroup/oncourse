package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.CertificateOutcomeStub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CertificateOutcomeUpdater extends AbstractWillowUpdater<CertificateOutcomeStub, CertificateOutcome> {
	
	private static final Logger logger = LogManager.getLogger();

	@Override
	protected void updateEntity(CertificateOutcomeStub stub, CertificateOutcome entity, RelationShipCallback callback) {
		entity.setCertificate(callback.updateRelationShip(stub.getCertificateId(), Certificate.class));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		Outcome outcome = callback.updateRelationShip(stub.getOutcomeId(), Outcome.class);
		if (outcome == null) {
			logger.error("Couldn't find Outcome with angelId={} for CertificateOutcome with angelId={} created on {} and modified on {}", stub.getOutcomeId(), stub.getAngelId(), stub.getCreated(), stub.getModified());
		}
		entity.setOutcome(outcome);
	}
}
