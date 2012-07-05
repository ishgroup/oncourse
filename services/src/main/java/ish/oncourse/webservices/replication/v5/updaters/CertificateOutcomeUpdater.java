package ish.oncourse.webservices.replication.v5.updaters;

import org.apache.log4j.Logger;

import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.CertificateOutcomeStub;

public class CertificateOutcomeUpdater extends AbstractWillowUpdater<CertificateOutcomeStub, CertificateOutcome> {
	
	private static final Logger logger = Logger.getLogger(CertificateOutcomeUpdater.class);

	@Override
	protected void updateEntity(CertificateOutcomeStub stub, CertificateOutcome entity, RelationShipCallback callback) {
		entity.setCertificate(callback.updateRelationShip(stub.getCertificateId(), Certificate.class));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		Outcome outcome = callback.updateRelationShip(stub.getOutcomeId(), Outcome.class);
		if (outcome == null) {
			logger.error("Couldn't find Outcome with angelId=" + stub.getOutcomeId() + 
					" for CertificateOutcome with angelId=" + stub.getAngelId() + 
					", created on " + stub.getCreated() + ", modified on " + stub.getModified());
		}
		entity.setOutcome(outcome);
	}
}
