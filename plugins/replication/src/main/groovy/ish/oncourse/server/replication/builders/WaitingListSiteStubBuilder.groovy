/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.WaitingListSite
import ish.oncourse.webservices.v21.stubs.replication.WaitingListSiteStub

/**
 */
class WaitingListSiteStubBuilder extends AbstractAngelStubBuilder<WaitingListSite, WaitingListSiteStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected WaitingListSiteStub createFullStub(WaitingListSite entity) {
		def stub = new WaitingListSiteStub()
		stub.setSiteId(entity.getSite().getId())
		stub.setWaitingListId(entity.getWaitingList().getId())
		return stub
	}
}
