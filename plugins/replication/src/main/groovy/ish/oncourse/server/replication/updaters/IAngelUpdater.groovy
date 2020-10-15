/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.webservices.util.GenericReplicationStub

/**
 */
interface IAngelUpdater {
	void updateEntityFromStub(GenericReplicationStub stub, Queueable entity, RelationShipCallback callback)
}
