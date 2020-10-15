/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.webservices.v21.stubs.replication.MembershipStub

/**
 */
class MembershipUpdater extends AbstractProductItemUpdater<MembershipStub, Membership> {

	@Override
	protected void updateEntity(MembershipStub stub, Membership entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback)

		entity.setProduct(callback.updateRelationShip(stub.getProductId(), MembershipProduct.class))
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class))
		entity.setExpiryDate(stub.getExpiryDate())
	}
}
