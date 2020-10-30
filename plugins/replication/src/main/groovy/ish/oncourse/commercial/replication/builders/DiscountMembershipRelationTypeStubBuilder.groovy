/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.DiscountMembershipRelationType
import ish.oncourse.webservices.v22.stubs.replication.DiscountMembershipRelationTypeStub

/**
 */
class DiscountMembershipRelationTypeStubBuilder extends AbstractAngelStubBuilder<DiscountMembershipRelationType, DiscountMembershipRelationTypeStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected DiscountMembershipRelationTypeStub createFullStub(final DiscountMembershipRelationType entity) {
		final def stub = new DiscountMembershipRelationTypeStub()
		stub.setContactRelationTypeId(entity.getContactRelationType().getId())
		stub.setCreated(entity.getCreatedOn())
		stub.setAngelId(entity.getId())
		if (entity.getDiscountMembership() != null) {
			stub.setMembershipDiscountId(entity.getDiscountMembership().getId())
		}
		stub.setModified(entity.getModifiedOn())
		stub.setWillowId(entity.getWillowId())
		return stub
	}

}
