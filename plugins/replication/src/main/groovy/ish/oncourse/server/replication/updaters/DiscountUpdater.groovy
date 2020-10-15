/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.Discount
import ish.oncourse.webservices.v21.stubs.replication.DiscountStub

/**
 */
class DiscountUpdater extends AbstractAngelUpdater<DiscountStub, Discount> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(DiscountStub stub, Discount entity, RelationShipCallback callback) {
		// discount is not supposed to be updated at willow
	}

}
