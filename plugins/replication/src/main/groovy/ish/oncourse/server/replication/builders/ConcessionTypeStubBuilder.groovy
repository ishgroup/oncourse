/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.ConcessionType
import ish.oncourse.webservices.v21.stubs.replication.ConcessionTypeStub

/**
 */
class ConcessionTypeStubBuilder extends AbstractAngelStubBuilder<ConcessionType, ConcessionTypeStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected ConcessionTypeStub createFullStub(ConcessionType t) {
		def stub = new ConcessionTypeStub()
		stub.setCreated(t.getCreatedOn())
		stub.setModified(t.getModifiedOn())
		stub.setHasConcessionNumber(t.getHasConcessionNumber())
		stub.setHasExpiryDate(t.getHasExpiryDate())
		stub.setName(t.getName())
		stub.setIsConcession(true)
		stub.setIsEnabled(t.getIsEnabled())
		if (t.getCreatedBy() != null) {
			stub.setCreatedBy(t.getCreatedBy().getId())
		}
		return stub
	}
}
