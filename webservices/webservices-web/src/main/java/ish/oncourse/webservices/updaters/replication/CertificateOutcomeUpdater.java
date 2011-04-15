package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.v4.stubs.replication.CertificateOutcomeStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class CertificateOutcomeUpdater extends AbstractWillowUpdater<CertificateOutcomeStub, CertificateOutcome> {

	@Override
	protected void updateEntity(CertificateOutcomeStub stub, CertificateOutcome entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCertificate(updateRelationShip(stub.getCertificateId(), Certificate.class, result));
		entity.setCollege(college);
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setOutcome(updateRelationShip(stub.getOutcomeId(), Outcome.class, result));
	}
}
