/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Preference
import ish.oncourse.webservices.v21.stubs.replication.PreferenceStub

/**
 */
class PreferenceStubBuilder extends AbstractAngelStubBuilder<Preference, PreferenceStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected PreferenceStub createFullStub(Preference entity) {
		def stub = new PreferenceStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setName(entity.getName())
		stub.setValue(entity.getValue())
		stub.setValueString(entity.getValueString())
		return stub
	}
}
