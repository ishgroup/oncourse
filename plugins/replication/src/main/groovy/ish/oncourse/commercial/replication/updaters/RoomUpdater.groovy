/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Site
import ish.oncourse.webservices.v22.stubs.replication.RoomStub

/**
 */
class RoomUpdater extends AbstractAngelUpdater<RoomStub, Room> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(RoomStub stub, Room entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setSeatedCapacity(stub.getCapacity())
		if (stub.getDirectionsTextile() != null) {
			entity.setDirections(stub.getDirectionsTextile())
		}
		if (stub.getFacilitiesTextile() != null) {
			entity.setFacilities(stub.getFacilitiesTextile())
		}
		entity.setName(stub.getName())
		entity.setSite(callback.updateRelationShip(stub.getSiteId(), Site.class))
	}
}
