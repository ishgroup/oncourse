package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.Room;
import ish.oncourse.model.Site;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.RoomStub;

public class RoomUpdater extends AbstractWillowUpdater<RoomStub, Room> {

	private ITextileConverter textileConverter;

	public RoomUpdater(ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(RoomStub stub, Room entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setCapacity(stub.getCapacity());
		if (stub.getDirectionsTextile() != null) {
			entity.setDirections(textileConverter.convertCoreTextile(stub.getDirectionsTextile()));
		}
		entity.setDirectionsTextile(stub.getDirectionsTextile());
		if (stub.getFacilitiesTextile() != null) {
			entity.setFacilities(textileConverter.convertCoreTextile(stub.getFacilitiesTextile()));
		}
		entity.setFacilitiesTextile(stub.getFacilitiesTextile());
		entity.setFacilities(stub.getFacilities());
		entity.setName(stub.getName());
		entity.setSite(callback.updateRelationShip(stub.getSiteId(), Site.class));

	}
}
