package ish.oncourse.webservices.replication.v22.updaters;

import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v22.stubs.replication.RoomStub;

public class RoomUpdater extends AbstractWillowUpdater<RoomStub, Room> {

	private IRichtextConverter textileConverter;

	public RoomUpdater(IRichtextConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(RoomStub stub, Room entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setCapacity(stub.getCapacity());
		if (stub.getDirectionsTextile() != null) {
			entity.setDirections(textileConverter.convertCoreText(stub.getDirectionsTextile()));
		}
		entity.setDirectionsTextile(stub.getDirectionsTextile());
		if (stub.getFacilitiesTextile() != null) {
			entity.setFacilities(textileConverter.convertCoreText(stub.getFacilitiesTextile()));
		}
		entity.setFacilitiesTextile(stub.getFacilitiesTextile());
		entity.setFacilities(stub.getFacilities());
		entity.setName(stub.getName());
		entity.setSite(callback.updateRelationShip(stub.getSiteId(), Site.class));

	}
}
