package ish.oncourse.webservices.replication.v21.updaters;

import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.services.IReachtextConverter;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v21.stubs.replication.RoomStub;

public class RoomUpdater extends AbstractWillowUpdater<RoomStub, Room> {

	private IReachtextConverter textileConverter;

	public RoomUpdater(IReachtextConverter textileConverter) {
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
