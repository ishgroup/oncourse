/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Room
import ish.oncourse.webservices.v21.stubs.replication.RoomStub

/**
 */
class RoomStubBuilder extends AbstractAngelStubBuilder<Room, RoomStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected RoomStub createFullStub(Room r) {
		def stub = new RoomStub()
		stub.setCreated(r.getCreatedOn())
		stub.setModified(r.getModifiedOn())
		stub.setDirectionsTextile(r.getDirections())
		stub.setFacilitiesTextile(r.getFacilities())
		stub.setName(r.getName())
		stub.setSiteId(r.getSite().getId())
		stub.setCapacity(r.getSeatedCapacity())
		return stub
	}

}
