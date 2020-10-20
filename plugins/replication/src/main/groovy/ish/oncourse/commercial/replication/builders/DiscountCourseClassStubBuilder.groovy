/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.DiscountCourseClass
import ish.oncourse.webservices.v21.stubs.replication.DiscountCourseClassStub

/**
 */
class DiscountCourseClassStubBuilder extends AbstractAngelStubBuilder<DiscountCourseClass, DiscountCourseClassStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected DiscountCourseClassStub createFullStub(DiscountCourseClass entity) {
		def stub = new DiscountCourseClassStub()
		stub.setCourseClassId(entity.getCourseClass().getId())
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setDiscountId(entity.getDiscount().getId())
		if (entity.getDiscountDollar() != null) {
			stub.setDiscountAmount(entity.getDiscountDollar().toBigDecimal())
		}
		return stub
	}
}
