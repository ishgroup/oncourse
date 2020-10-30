/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.DiscountMembership
import ish.oncourse.webservices.v22.stubs.replication.DiscountMembershipStub

/**
 */
class DiscountMembershipStubBuilder extends AbstractAngelStubBuilder<DiscountMembership, DiscountMembershipStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected DiscountMembershipStub createFullStub(final DiscountMembership entity) {
		final def stub = new DiscountMembershipStub()
		stub.setApplyToMemberOnly(entity.getApplyToMemberOnly())
		stub.setCreated(entity.getCreatedOn())
		stub.setDiscountId(entity.getDiscount().getId())
		stub.setMembershipProductId(entity.getMembershipProduct().getId())
		stub.setModified(entity.getModifiedOn())
		stub.setAngelId(entity.getId())
		stub.setWillowId(entity.getWillowId())
		return stub
	}
}
