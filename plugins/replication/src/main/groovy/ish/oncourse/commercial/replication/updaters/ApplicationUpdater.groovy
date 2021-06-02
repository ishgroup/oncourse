/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.common.types.ApplicationStatus
import ish.common.types.ConfirmationStatus
import ish.common.types.PaymentSource
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.server.cayenne.Application
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.Student
import ish.oncourse.webservices.v23.stubs.replication.ApplicationStub

@CompileStatic
class ApplicationUpdater extends AbstractAngelUpdater<ApplicationStub, Application> {

	@Override
	protected void updateEntity(ApplicationStub stub, Application entity, RelationShipCallback callback) {

		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class))
		entity.setCourse(callback.updateRelationShip(stub.getCourseId(), Course.class))
		entity.setSource(TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class))
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), ApplicationStatus.class))
		entity.setEnrolBy(stub.getEnrolBy())
		if (stub.getFeeOverride() != null) {
			entity.setFeeOverride(new Money(stub.getFeeOverride()))
		}
		entity.setReason(stub.getReason())

		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		if (stub.getConfirmationStatus() != null) {
			entity.setConfirmationStatus(TypesUtil.getEnumForDatabaseValue(stub.getConfirmationStatus(), ConfirmationStatus.class))
		}
	}
}
