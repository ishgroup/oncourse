/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.CorporatePassProduct
import ish.oncourse.webservices.v21.stubs.replication.CorporatePassProductStub

class CorporatePassProductStubBuilder extends AbstractAngelStubBuilder<CorporatePassProduct, CorporatePassProductStub> {
	@Override
	protected CorporatePassProductStub createFullStub(CorporatePassProduct entity) {
		def stub = new CorporatePassProductStub()

		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setCorporatePassId(entity.getCorporatePass().getId())
		stub.setProductId(entity.getProduct().getId())

		return stub

	}
}
