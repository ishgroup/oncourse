package ish.oncourse.webservices.replication.v4.updaters;

import ish.oncourse.model.ContactRelationType;
import ish.oncourse.model.DiscountMembership;
import ish.oncourse.model.DiscountMembershipRelationType;
import ish.oncourse.webservices.v4.stubs.replication.DiscountMembershipRelationTypeStub;

public class DiscountMembershipRelationTypeUpdater extends AbstractWillowUpdater<DiscountMembershipRelationTypeStub, DiscountMembershipRelationType> {

	@Override
	protected void updateEntity(final DiscountMembershipRelationTypeStub stub, final DiscountMembershipRelationType entity, final RelationShipCallback callback) {
		final ContactRelationType contactRelationType = callback.updateRelationShip(stub.getContactRelationTypeId(), ContactRelationType.class);
		entity.setContactRelationType(contactRelationType);
		entity.setCreated(stub.getCreated());
		final DiscountMembership discountMembership = callback.updateRelationShip(stub.getMembershipDiscountId(), DiscountMembership.class);
		entity.setDiscountMembership(discountMembership);
		entity.setModified(stub.getModified());
	}

}
