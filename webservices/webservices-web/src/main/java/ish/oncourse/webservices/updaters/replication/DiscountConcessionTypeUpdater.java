package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.webservices.v4.stubs.replication.DiscountConcessionTypeStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class DiscountConcessionTypeUpdater extends AbstractWillowUpdater<DiscountConcessionTypeStub, DiscountConcessionType> {

	@SuppressWarnings("unchecked")
	@Override
	protected void updateEntity(DiscountConcessionTypeStub stub, DiscountConcessionType entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(college);
		entity.setConcessionType((ConcessionType) updateRelationShip(stub.getConcessionTypeId(), "ConcessionType", result));
		entity.setCreated(stub.getCreated());
		entity.setDiscount((Discount) updateRelationShip(stub.getDiscountId(), "Discount", result));
		entity.setModified(stub.getModified());
	}
}
