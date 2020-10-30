/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.WaitingList
import ish.oncourse.webservices.v22.stubs.replication.WaitingListStub

/**
 */
class WaitingListUpdater extends AbstractAngelUpdater<WaitingListStub, WaitingList> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(WaitingListStub stub, WaitingList entity, RelationShipCallback callback) {
		entity.setCourse(callback.updateRelationShip(stub.getCourseId(), Course.class))
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class))
		entity.setStudentNotes(stub.getDetail())
		entity.setStudentCount(stub.getStudentCount())
	}
}
