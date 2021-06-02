/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.webservices.v23.stubs.replication.MembershipProductStub

/**
 */
class MembershipProductStubBuilder extends AbstractProductStubBuilder<MembershipProduct, MembershipProductStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected MembershipProductStub createFullStub(final MembershipProduct entity) {
		final def stub = super.createFullStub(entity) as MembershipProductStub

		stub.setExpiryDays(entity.getExpiryDays())
		stub.setExpiryType(entity.getExpiryType().getDatabaseValue())

		return stub
	}

	@Override
	protected MembershipProductStub createStub() {
		return new MembershipProductStub()
	}
}
