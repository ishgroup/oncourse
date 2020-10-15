/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.VoucherProductCourse
import ish.oncourse.webservices.v21.stubs.replication.VoucherProductCourseStub

/**
 */
class VoucherProductCourseStubBuilder extends AbstractAngelStubBuilder<VoucherProductCourse, VoucherProductCourseStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected VoucherProductCourseStub createFullStub(final VoucherProductCourse entity) {
		def stub = new VoucherProductCourseStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setCourseId(entity.getCourse().getId())
		stub.setVoucherProductId(entity.getVoucherProduct().getId())
		return stub
	}

}
