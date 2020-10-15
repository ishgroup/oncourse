/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.Queueable

/**
 */
interface RelationShipCallback {
	def <T extends Queueable> T updateRelationShip(Long entityId, Class<T> clazz)
}
