package ish.oncourse.webservices.replication.updaters;

import org.apache.cayenne.Cayenne;

import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.webservices.v4.stubs.replication.RoomStub;

public class RoomUpdater extends AbstractWillowUpdater<RoomStub, Room> {

	@Override
	protected void updateEntity(RoomStub stub, Room entity, RelationShipCallback callback) {	
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		entity.setCapacity(stub.getCapacity());
		entity.setDirections(stub.getDirections());
		entity.setDirectionsTextile(stub.getDirectionsTextile());
		entity.setFacilities(stub.getFacilities());
		entity.setName(stub.getName());
		
		Long siteId = stub.getSiteId();
		if (siteId != null) {
			Site s = Cayenne.objectForPK(entity.getObjectContext(), Site.class, siteId);
			entity.setSite(s);
		}
	}
}
