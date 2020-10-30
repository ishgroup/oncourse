/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.webservices.v22.stubs.replication.CourseClassStub

/**
 */
class CourseClassUpdater extends AbstractAngelUpdater<CourseClassStub, CourseClass> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(CourseClassStub stub, CourseClass entity, RelationShipCallback callback) {
		// skip any updates because the courseClass can't be created in willow for now
	}

}
