package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.ConcessionTypeStub;

import java.util.Map;

public class ConcessionTypeStubBuilder extends AbstractWillowStubBuilder<ConcessionType, ConcessionTypeStub> {
	
	public ConcessionTypeStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowQueueService queueService, IWillowStubBuilder next) {
		super(queue, queueService, next);
	}

	@Override
	protected ConcessionTypeStub createFullStub(ConcessionType entity) {
		ConcessionTypeStub stub = new ConcessionTypeStub();
		
		stub.setWillowId(entity.getId());
		stub.setCreated(entity.getCreated());
		stub.setCredentialExpiryDays(entity.getCredentialExpiryDays());
		stub.setHasConcessionNumber(entity.getHasConcessionNumber());
		stub.setHasExpiryDate(entity.getHasExpiryDate());
		stub.setIsConcession(entity.getIsConcession());
		stub.setIsEnabled(entity.getIsEnabled());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setRequiresCredentialCheck(entity.getRequiresCredentialCheck());
	
		return stub;
	}
}
