package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountMembership;
import ish.oncourse.model.MembershipProduct;
import ish.oncourse.webservices.v4.stubs.replication.DiscountMembershipStub;

public class DiscountMembershipUpdater extends AbstractWillowUpdater<DiscountMembershipStub, DiscountMembership> {

	@Override
	protected void updateEntity(final DiscountMembershipStub stub, final DiscountMembership entity, final RelationShipCallback callback) {
		entity.setApplyToMemberOnly(stub.isApplyToMemberOnly());
		entity.setCreated(stub.getCreated());
		final Discount discount = callback.updateRelationShip(stub.getDiscountId(), Discount.class);
		entity.setDiscount(discount);
		final MembershipProduct membershipProduct = callback.updateRelationShip(stub.getMembershipProductId(), MembershipProduct.class);
		entity.setMembershipProduct(membershipProduct);
		entity.setModified(stub.getModified());
	}

}
