/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.ConcessionType
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.StudentConcession
import ish.oncourse.webservices.v23.stubs.replication.StudentConcessionStub

/**
 */
class StudentConcessionUpdater extends AbstractAngelUpdater<StudentConcessionStub, StudentConcession> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(StudentConcessionStub stub, StudentConcession entity, RelationShipCallback callback) {
		entity.setAuthorisationExpiresOn(stub.getAuthorisationExpiresOn())
		entity.setAuthorisedOn(stub.getAuthorisedOn())
		entity.setConcessionNumber(stub.getConcessionNumber())
		entity.setConcessionType(callback.updateRelationShip(stub.getConcessionTypeId(), ConcessionType.class))
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setExpiresOn(stub.getExpiresOn())
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class))
	}

}
