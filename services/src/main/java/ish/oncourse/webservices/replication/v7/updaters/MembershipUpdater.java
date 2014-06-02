package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Membership;
import ish.oncourse.model.MembershipProduct;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.MembershipStub;

public class MembershipUpdater extends AbstractProductItemUpdater<MembershipStub, Membership> {

	@Override
	protected void updateEntity(final MembershipStub stub, final Membership entity, final RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		entity.setProduct(callback.updateRelationShip(stub.getProductId(), MembershipProduct.class));
		entity.setExpiryDate(stub.getExpiryDate());
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
	}

}
