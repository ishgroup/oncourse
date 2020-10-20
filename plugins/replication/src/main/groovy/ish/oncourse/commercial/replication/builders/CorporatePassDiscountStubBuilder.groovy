/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.CorporatePassDiscount
import ish.oncourse.webservices.v21.stubs.replication.CorporatePassDiscountStub

class CorporatePassDiscountStubBuilder extends AbstractAngelStubBuilder<CorporatePassDiscount, CorporatePassDiscountStub> {

	@Override
	protected CorporatePassDiscountStub createFullStub(CorporatePassDiscount entity) {
		def stub = new CorporatePassDiscountStub()

		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setCorporatePassId(entity.getCorporatePass().getId())
		stub.setDiscountId(entity.getDiscount().getId())

		return stub
	}
}
