package ish.oncourse.webservices.replication.updaters;

import org.apache.cayenne.Cayenne;

import ish.oncourse.model.Country;
import ish.oncourse.model.Site;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.v4.stubs.replication.SiteStub;

public class SiteUpdater extends AbstractWillowUpdater<SiteStub, Site> {

	private ITextileConverter textileConverter;

	public SiteUpdater(ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(SiteStub stub, Site entity, RelationShipCallback callback) {

		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());

		Long countryId = stub.getCountryId();
		if (countryId != null) {
			Country c = Cayenne.objectForPK(entity.getObjectContext(), Country.class, countryId);
			entity.setCountry(c);
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
	}

}
