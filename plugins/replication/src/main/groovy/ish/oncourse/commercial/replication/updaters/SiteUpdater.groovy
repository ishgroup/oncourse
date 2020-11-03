/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Site
import ish.oncourse.commercial.replication.reference.ReferenceUtil
import ish.oncourse.webservices.v22.stubs.replication.SiteStub

/**
 */
class SiteUpdater extends AbstractAngelUpdater<SiteStub, Site> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(SiteStub stub, Site entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		Country country = null
		def countryId = stub.getCountryId()
		if (countryId != null) {
			country = ReferenceUtil.findCountryByWillowId(entity.getObjectContext(), countryId)
		}
		entity.setCountry(country)
		entity.setDrivingDirections(stub.getDrivingDirections())
		entity.setLatitude(stub.getLatitude())
		entity.setLongitude(stub.getLongitude())
		entity.setName(stub.getName())
		entity.setPostcode(stub.getPostcode())
		entity.setPublicTransportDirections(stub.getPublicTransportDirections())
		entity.setSpecialInstructions(stub.getSpecialInstructions())
		entity.setState(stub.getState())
		entity.setStreet(stub.getStreet())
		entity.setSuburb(stub.getSuburb())
		entity.setLocalTimezone(stub.getTimeZone())
		entity.setIsVirtual(stub.isVirtual())
	}
}
