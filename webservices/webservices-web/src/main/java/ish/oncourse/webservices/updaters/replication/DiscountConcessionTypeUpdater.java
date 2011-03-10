package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.DiscountConcessionTypeStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class DiscountConcessionTypeUpdater extends AbstractWillowUpdater<DiscountConcessionTypeStub, DiscountConcessionType> {

	public DiscountConcessionTypeUpdater(College college, IWillowQueueService queueService,
			@SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}

	@Override
	protected void updateEntity(DiscountConcessionTypeStub stub, DiscountConcessionType entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(getCollege(entity.getObjectContext()));
		entity.setConcessionType((ConcessionType) updateRelatedEntity(entity.getObjectContext(), stub.getConcessionType(), relationStubs));
		entity.setCreated(stub.getCreated());
		entity.setDiscount((Discount) updateRelatedEntity(entity.getObjectContext(), stub.getDiscount(), relationStubs));
		entity.setModified(stub.getModified());
	}
}
