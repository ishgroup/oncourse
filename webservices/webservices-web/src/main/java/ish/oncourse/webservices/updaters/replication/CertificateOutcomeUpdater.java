package ish.oncourse.webservices.updaters.replication;

import java.util.List;

import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.College;
import ish.oncourse.model.Outcome;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.CertificateOutcomeStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

public class CertificateOutcomeUpdater extends AbstractWillowUpdater<CertificateOutcomeStub, CertificateOutcome> {
	
	public CertificateOutcomeUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}

	@Override
	protected void updateEntity(CertificateOutcomeStub stub, CertificateOutcome entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setCertificate((Certificate) updateRelatedEntity(entity.getObjectContext(), stub.getCertificate(), relationStubs));
		entity.setCollege(getCollege(entity.getObjectContext()));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setOutcome((Outcome) updateRelatedEntity(entity.getObjectContext(), stub.getOutcome(), relationStubs));
	}
}
