package ish.oncourse.webservices.replication.v22.updaters;

import ish.oncourse.model.Country;
import ish.oncourse.model.Site;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v22.stubs.replication.SiteStub;
import org.apache.cayenne.Cayenne;
import org.apache.commons.lang3.StringUtils;

public class SiteUpdater extends AbstractWillowUpdater<SiteStub, Site> {

	private IRichtextConverter textileConverter;

	public SiteUpdater(IRichtextConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(SiteStub stub, Site entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		if (stub.getCountryId() != null) {
			entity.setCountry(Cayenne.objectForPK(entity.getObjectContext(), Country.class, stub.getCountryId()));
		}
		entity.setIsWebVisible(stub.isWebVisible());
		entity.setLatitude(stub.getLatitude());
		entity.setLongitude(stub.getLongitude());
		entity.setName(stub.getName());
		entity.setPostcode(stub.getPostcode());
		entity.setState(stub.getState());
		entity.setStreet(stub.getStreet());
		entity.setSuburb(stub.getSuburb());
		entity.setTimeZone(stub.getTimeZone());
		entity.setIsVirtual(stub.isVirtual());

		entity.setDrivingDirectionsTextile(stub.getDrivingDirections());
		entity.setDrivingDirections(
				convertTextileOrNull(stub.getDrivingDirections()));

		entity.setPublicTransportDirectionsTextile(stub.getPublicTransportDirections());
		entity.setPublicTransportDirections(
				convertTextileOrNull(stub.getPublicTransportDirections()));

		entity.setSpecialInstructionsTextile(stub.getSpecialInstructions());
		entity.setSpecialInstructions(
				convertTextileOrNull(stub.getSpecialInstructions()));
	}

	public String convertTextileOrNull(String value) {
		String res = null;
		if (value != null && !StringUtils.EMPTY.equals(value)) {
			res = textileConverter.convertCoreText(value);
		}
		return res;
	}

}
