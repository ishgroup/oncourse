package ish.oncourse.webservices.replication.v9.updaters;

import ish.oncourse.model.ContactRelationType;
import ish.oncourse.model.DiscountMembership;
import ish.oncourse.model.DiscountMembershipRelationType;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v9.stubs.replication.DiscountMembershipRelationTypeStub;

public class DiscountMembershipRelationTypeUpdater extends AbstractWillowUpdater<DiscountMembershipRelationTypeStub, DiscountMembershipRelationType> {

	@Override
	protected void updateEntity(final DiscountMembershipRelationTypeStub stub, final DiscountMembershipRelationType entity, final RelationShipCallback callback) {
		entity.setContactRelationType(callback.updateRelationShip(stub.getContactRelationTypeId(), ContactRelationType.class));
		entity.setCreated(stub.getCreated());
		entity.setDiscountMembership(callback.updateRelationShip(stub.getMembershipDiscountId(), DiscountMembership.class));
		entity.setModified(stub.getModified());
	}

}
