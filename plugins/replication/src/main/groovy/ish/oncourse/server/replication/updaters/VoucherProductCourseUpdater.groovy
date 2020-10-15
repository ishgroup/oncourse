/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.server.cayenne.VoucherProductCourse
import ish.oncourse.webservices.v21.stubs.replication.VoucherProductCourseStub

/**
 */
class VoucherProductCourseUpdater extends AbstractAngelUpdater<VoucherProductCourseStub, VoucherProductCourse> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(final VoucherProductCourseStub stub, final VoucherProductCourse entity, final RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setCourse(callback.updateRelationShip(stub.getCourseId(), Course.class))
		entity.setVoucherProduct(callback.updateRelationShip(stub.getVoucherProductId(), VoucherProduct.class))
	}

}
