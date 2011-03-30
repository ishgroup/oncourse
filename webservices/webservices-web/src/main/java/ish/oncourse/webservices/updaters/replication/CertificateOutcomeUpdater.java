package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.v4.stubs.replication.CertificateOutcomeStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class CertificateOutcomeUpdater extends AbstractWillowUpdater<CertificateOutcomeStub, CertificateOutcome> {

	@SuppressWarnings("unchecked")
	@Override
	protected void updateEntity(CertificateOutcomeStub stub, CertificateOutcome entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCertificate((Certificate) updateRelationShip(stub.getCertificateId(), "Certificate", result));
		entity.setCollege(college);
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setOutcome((Outcome) updateRelationShip(stub.getOutcomeId(), "Outcome", result));
	}
}
