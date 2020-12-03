/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.webservices.v23.stubs.replication.SystemUserStub

/**
 */
class SystemUserUpdater extends AbstractAngelUpdater<SystemUserStub, SystemUser> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(SystemUserStub stub, SystemUser entity, RelationShipCallback callback) {
		entity.setCanEditCMS(stub.isEditCMS())
		entity.setCanEditTara(stub.isEditTara())
		def siteId = stub.getDefaultAdministrationCentreId()
		if (siteId != null) {
			entity.setDefaultAdministrationCentre(callback.updateRelationShip(siteId, Site.class))
		}
		entity.setEmail(stub.getEmail())
		entity.setFirstName(stub.getFirstName())
		entity.setIsActive(stub.isIsActive())
		entity.setIsAdmin(stub.isIsAdmin())
		entity.setLastLoginIP(stub.getLastLoginIP())
		entity.setLastLoginOn(stub.getLastLoginOn())
		entity.setLastName(stub.getSurname())
		entity.setLogin(stub.getLogin())
		entity.setPassword(stub.getPassword())
		entity.setToken(stub.getToken())
		entity.setTokenScratchCodes(stub.getTokenScratchCodes())
	}

}
