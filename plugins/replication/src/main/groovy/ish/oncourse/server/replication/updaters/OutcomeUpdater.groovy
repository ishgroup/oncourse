/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters


import ish.common.types.OutcomeStatus
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.PriorLearning
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.webservices.v21.stubs.replication.OutcomeStub
import ish.util.LocalDateUtils

/**
 */
class OutcomeUpdater extends AbstractAngelUpdater<OutcomeStub, Outcome> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(OutcomeStub stub, Outcome entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())

		if (stub.getEndDate() != null) {
			entity.setEndDate(LocalDateUtils.dateToValue(stub.getEndDate()))
			entity.setEndDateOverridden(true)
		}

		if (stub.getEnrolmentId() != null) {
			def enrolment = callback.updateRelationShip(stub.getEnrolmentId(), Enrolment.class)
			entity.setEnrolment(enrolment)
		} else if (stub.getPriorLearningId() != null) {
			def priorLearning = callback.updateRelationShip(stub.getPriorLearningId(), PriorLearning.class)
			entity.setPriorLearning(priorLearning)
		}
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), OutcomeStatus.class))
		entity.setMarkedByTutorDate(stub.getMarkedByTutorDate())
		if (stub.getMarkedByTutorId() != null) {
			entity.setMarkedByTutor(callback.updateRelationShip(stub.getMarkedByTutorId(), Tutor.class))
		}
	}

}
