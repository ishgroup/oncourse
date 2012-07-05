package ish.oncourse.webservices.replication.v4.updaters;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.webservices.v4.stubs.replication.DiscountConcessionTypeStub;

public class DiscountConcessionTypeUpdater extends AbstractWillowUpdater<DiscountConcessionTypeStub, DiscountConcessionType> {

	@Override
	protected void updateEntity(DiscountConcessionTypeStub stub, DiscountConcessionType entity, RelationShipCallback callback) {
		entity.setConcessionType(callback.updateRelationShip(stub.getConcessionTypeId(), ConcessionType.class));
		entity.setCreated(stub.getCreated());
		entity.setDiscount(callback.updateRelationShip(stub.getDiscountId(), Discount.class));
		entity.setModified(stub.getModified());
	}
}
