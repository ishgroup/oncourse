/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Site
import ish.oncourse.webservices.v21.stubs.replication.SiteStub

/**
 */
class SiteStubBuilder extends AbstractAngelStubBuilder<Site, SiteStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected SiteStub createFullStub(Site s) {
		def stub = new SiteStub()
		stub.setCreated(s.getCreatedOn())
		stub.setModified(s.getModifiedOn())
		if (s.getCountry() != null) {
			stub.setCountryId(s.getCountry().getWillowId())
		}
		stub.setDrivingDirections(s.getDrivingDirections())
		stub.setLatitude(s.getLatitude())
		stub.setLongitude(s.getLongitude())
		stub.setName(s.getName())
		stub.setPostcode(s.getPostcode())
		stub.setPublicTransportDirections(s.getPublicTransportDirections())
		stub.setSpecialInstructions(s.getSpecialInstructions())
		stub.setState(s.getState())
		stub.setStreet(s.getStreet())
		stub.setSuburb(s.getSuburb())
		stub.setWebVisible(s.getIsShownOnWeb())
		stub.setTimeZone(s.getLocalTimezone())
		stub.setVirtual(s.getIsVirtual())
		return stub
	}

}
