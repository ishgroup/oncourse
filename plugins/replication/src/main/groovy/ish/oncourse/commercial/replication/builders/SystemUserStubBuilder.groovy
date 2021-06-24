/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.webservices.v23.stubs.replication.SystemUserStub

/**
 */
class SystemUserStubBuilder extends AbstractAngelStubBuilder<SystemUser, SystemUserStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected SystemUserStub createFullStub(SystemUser entity) {
		def stub = new SystemUserStub()
		if (entity.getDefaultAdministrationCentre() != null) {
			stub.setDefaultAdministrationCentreId(entity.getDefaultAdministrationCentre().getId())
		}
		stub.setEditCMS(entity.getCanEditCMS())
		stub.setEditTara(entity.getCanEditTara())
		stub.setEmail(entity.getEmail())
		stub.setFirstName(entity.getFirstName())
		stub.setIsActive(entity.getIsActive())
		stub.setIsAdmin(entity.getIsAdmin())
		stub.setLogin(entity.getLogin())
		stub.setPassword(entity.getPassword())
		stub.setSurname(entity.getLastName())
		stub.setLastLoginIP(entity.getLastLoginIP())
		stub.setLastLoginOn(entity.getLastLoginOn())
		stub.setToken(entity.getToken())
		stub.setTokenScratchCodes(entity.getTokenScratchCodes())
		stub.sessionId = entity.sessionId

		return stub
	}

}
