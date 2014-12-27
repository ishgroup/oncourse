package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.Country;
import ish.oncourse.model.Site;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.SiteStub;
import org.apache.cayenne.Cayenne;

public class SiteUpdater extends AbstractWillowUpdater<SiteStub, Site> {

	private ITextileConverter textileConverter;

	public SiteUpdater(ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(SiteStub stub, Site entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		if (stub.getCountryId() != null) {
			entity.setCountry(Cayenne.objectForPK(entity.getObjectContext(), Country.class, stub.getCountryId()));
		}
		String drivingDirections = stub.getDrivingDirections();
		if (drivingDirections != null) {
			entity.setDrivingDirections(textileConverter.convertCoreTextile(drivingDirections));
		}
		entity.setDrivingDirectionsTextile(drivingDirections);
		entity.setIsWebVisible(stub.isWebVisible());
		entity.setLatitude(stub.getLatitude());
		entity.setLongitude(stub.getLongitude());
		entity.setName(stub.getName());
		entity.setPostcode(stub.getPostcode());
		String publicTransportDirections = stub.getPublicTransportDirections();
		if (publicTransportDirections != null) {
			entity.setPublicTransportDirections(textileConverter.convertCoreTextile(publicTransportDirections));
		}
		entity.setPublicTransportDirectionsTextile(publicTransportDirections);
		String specialInstructions = stub.getSpecialInstructions();
		if (specialInstructions != null) {
			entity.setSpecialInstructions(textileConverter.convertCoreTextile(specialInstructions));
		}
		entity.setSpecialInstructionsTextile(specialInstructions);
		entity.setState(stub.getState());
		entity.setStreet(stub.getStreet());
		entity.setSuburb(stub.getSuburb());
		entity.setTimeZone(stub.getTimeZone());
		entity.setIsVirtual(stub.isVirtual());
	}

}
