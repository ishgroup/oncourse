/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.DiscountConcessionType
import ish.oncourse.webservices.v21.stubs.replication.DiscountConcessionTypeStub

/**
 */
class DiscountConcessionTypeStubBuilder extends AbstractAngelStubBuilder<DiscountConcessionType, DiscountConcessionTypeStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected DiscountConcessionTypeStub createFullStub(DiscountConcessionType entity) {
		def stub = new DiscountConcessionTypeStub()
		stub.setConcessionTypeId(entity.getConcessionType().getId())
		stub.setCreated(entity.getCreatedOn())
		stub.setDiscountId(entity.getDiscount().getId())
		stub.setModified(entity.getModifiedOn())
		return stub
	}
}
