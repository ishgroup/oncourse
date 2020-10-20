/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.server.cayenne.CorporatePassCourseClass
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.webservices.v21.stubs.replication.CorporatePassCourseClassStub

/**
 */
class CorporatePassCourseClassUpdater extends AbstractAngelUpdater<CorporatePassCourseClassStub, CorporatePassCourseClass> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(CorporatePassCourseClassStub stub, CorporatePassCourseClass entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setCorporatePass(callback.updateRelationShip(stub.getCorporatePassId(), CorporatePass.class))
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class))
	}

}
