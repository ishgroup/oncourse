package ish.oncourse.webservices.replication.v11.builders;

import ish.oncourse.model.Room;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v11.stubs.replication.RoomStub;

public class RoomStubBuilder extends AbstractWillowStubBuilder<Room, RoomStub> {

	@Override
	protected RoomStub createFullStub(Room entity) {
		RoomStub roomStub = new RoomStub();
		roomStub.setCapacity(entity.getCapacity());
		roomStub.setCreated(entity.getCreated());
		roomStub.setModified(entity.getModified());
		roomStub.setDirectionsTextile(entity.getDirectionsTextile());
		roomStub.setFacilitiesTextile(entity.getFacilitiesTextile());
		roomStub.setSiteId(entity.getSite().getId());
		roomStub.setName(entity.getName());
		return roomStub;
	}
}
