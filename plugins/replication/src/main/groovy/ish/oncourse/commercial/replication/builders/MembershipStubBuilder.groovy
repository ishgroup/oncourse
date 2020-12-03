/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Membership
import ish.oncourse.webservices.v23.stubs.replication.MembershipStub

/**
 */
class MembershipStubBuilder extends AbstractProductItemStubBuilder<Membership, MembershipStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected MembershipStub createFullStub(final Membership entity) {
		final MembershipStub stub = super.createFullStub(entity)

		stub.setExpiryDate(entity.getExpiryDate())
		stub.setContactId(entity.getContact().getId())
		return stub
	}

	@Override
	protected MembershipStub createStub() {
		return new MembershipStub()
	}
}
