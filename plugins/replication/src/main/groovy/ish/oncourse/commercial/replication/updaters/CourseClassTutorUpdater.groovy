/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.webservices.v21.stubs.replication.CourseClassTutorStub

/**
 */
class CourseClassTutorUpdater extends AbstractAngelUpdater<CourseClassTutorStub, CourseClassTutor> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(CourseClassTutorStub stub, CourseClassTutor entity, RelationShipCallback callback) {
		entity.setConfirmedOn(stub.getConfirmedOn())
		def courseClass = callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class)
		entity.setCourseClass(courseClass)
		entity.setCreatedOn(stub.getCreated())
		def tutor = callback.updateRelationShip(stub.getTutorId(), Tutor.class)
		entity.setTutor(tutor)
		entity.setInPublicity(stub.isInPublicity())
	}
}
