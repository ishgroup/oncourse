package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountMembership;
import ish.oncourse.model.MembershipProduct;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.DiscountMembershipStub;

public class DiscountMembershipUpdater extends AbstractWillowUpdater<DiscountMembershipStub, DiscountMembership> {

	@Override
	protected void updateEntity(final DiscountMembershipStub stub, final DiscountMembership entity, final RelationShipCallback callback) {
		entity.setApplyToMemberOnly(stub.isApplyToMemberOnly());
		entity.setCreated(stub.getCreated());
		entity.setDiscount(callback.updateRelationShip(stub.getDiscountId(), Discount.class));
		entity.setMembershipProduct(callback.updateRelationShip(stub.getMembershipProductId(), MembershipProduct.class));
		entity.setModified(stub.getModified());
	}

}
