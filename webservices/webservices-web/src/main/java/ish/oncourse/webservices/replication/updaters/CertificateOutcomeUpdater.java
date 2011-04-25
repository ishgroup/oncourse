package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.v4.stubs.replication.CertificateOutcomeStub;

public class CertificateOutcomeUpdater extends AbstractWillowUpdater<CertificateOutcomeStub, CertificateOutcome> {

	@Override
	protected void updateEntity(CertificateOutcomeStub stub, CertificateOutcome entity, RelationShipCallback callback) {
		entity.setAngelId(stub.getAngelId());
		entity.setCertificate(callback.updateRelationShip(stub.getCertificateId(), Certificate.class));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setOutcome(callback.updateRelationShip(stub.getOutcomeId(), Outcome.class));
	}
}
