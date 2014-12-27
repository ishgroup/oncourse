package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.Site;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.SiteStub;

public class SiteStubBuilder extends AbstractWillowStubBuilder<Site, SiteStub> {

	@Override
	protected SiteStub createFullStub(Site s) {
		SiteStub stub = new SiteStub();
		stub.setCreated(s.getCreated());
		stub.setModified(s.getModified());
		if (s.getCountry() != null) {
			stub.setCountryId(s.getCountry().getId());
		}
		stub.setDrivingDirections(s.getDrivingDirectionsTextile());
		stub.setLatitude(s.getLatitude());
		stub.setLongitude(s.getLongitude());
		stub.setName(s.getName());
		stub.setPostcode(s.getPostcode());
		stub.setPublicTransportDirections(s.getPublicTransportDirectionsTextile());
		stub.setSpecialInstructions(s.getSpecialInstructionsTextile());
		stub.setState(s.getState());
		stub.setStreet(s.getStreet());
		stub.setSuburb(s.getSuburb());
		stub.setTimeZone(s.getTimeZone());
		stub.setVirtual(s.getIsVirtual());
		return stub;
	}

}
