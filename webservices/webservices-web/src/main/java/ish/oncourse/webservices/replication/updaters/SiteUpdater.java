package ish.oncourse.webservices.replication.updaters;

import org.apache.cayenne.Cayenne;

import ish.oncourse.model.Country;
import ish.oncourse.model.Site;
import ish.oncourse.webservices.v4.stubs.replication.SiteStub;

public class SiteUpdater extends AbstractWillowUpdater<SiteStub, Site> {

	@Override
	protected void updateEntity(SiteStub stub, Site entity, RelationShipCallback callback) {	
	
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		Long countryId = stub.getCountryId();
		if (countryId != null) {
			Country c = Cayenne.objectForPK(entity.getObjectContext(), Country.class, countryId);
			entity.setCountry(c);
		}
		
		entity.setDrivingDirections(stub.getDrivingDirections());
		entity.setIsWebVisible(stub.isWebVisible());
		entity.setLatitude(stub.getLatitude());
		entity.setLongitude(stub.getLatitude());
		entity.setName(stub.getName());
		entity.setPostcode(stub.getPostcode());
		entity.setPublicTransportDirections(stub.getPublicTransportDirections());
		entity.setSpecialInstructions(stub.getSpecialInstructions());
		entity.setState(stub.getState());
		entity.setStreet(stub.getStreet());
		entity.setSuburb(stub.getSuburb());
	}

}
