/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.common.types.*
import ish.math.Money
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Student
import ish.oncourse.webservices.v21.stubs.replication.EnrolmentStub

/**
 */
class EnrolmentUpdater extends AbstractAngelUpdater<EnrolmentStub, Enrolment> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	void updateEntity(EnrolmentStub stub, Enrolment entity, RelationShipCallback callback) {
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class))
		entity.setCreatedOn(stub.getCreated())

		entity.setModifiedOn(stub.getModified())
		entity.setSource(TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class))
		final def statusString = stub.getStatus().replaceAll("PENDING", "IN_TRANSACTION")
		def status = EnrolmentStatus.valueOf(statusString)
		if (status == null) {
			final def message = "Failed to load enrolment status for status string = " + statusString + " with willowid = " + stub.getWillowId() +
					" status changed to corrupted!"
			LOG.debug(message, new Exception(message))
			throw new IllegalArgumentException(message, new Exception(message))
		}
		entity.setStatus(status)
		def student = callback.updateRelationShip(stub.getStudentId(), Student.class)
		entity.setStudent(student)
		entity.setStudyReason(TypesUtil.getEnumForDatabaseValue(stub.getReasonForStudy(), StudyReason.class))

		entity.setCreditOfferedValue(stub.getCreditOfferedValue())
		entity.setCreditProvider(stub.getCreditProvider())
		entity.setCreditUsedValue(stub.getCreditUsedValue())
		entity.setCreditFOEId(stub.getCreditFoeId())
		if (stub.getFeeStatus() != null) {
			entity.setFeeStatus(TypesUtil.getEnumForDatabaseValue(stub.getFeeStatus(), StudentStatusForUnitOfStudy.class))
		}
		if (stub.getCreditTotal() != null) {
			entity.setCreditTotal(TypesUtil.getEnumForDatabaseValue(stub.getCreditTotal(), RecognitionOfPriorLearningIndicator.class))
		}
		if (stub.getCreditType() != null) {
			entity.setCreditType(TypesUtil.getEnumForDatabaseValue(stub.getCreditType(), CreditType.class))
		}
		if (stub.getCreditLevel() != null) {
			entity.setCreditLevel(TypesUtil.getEnumForDatabaseValue(stub.getCreditLevel(), CreditLevel.class))
		}
		if (stub.getCreditProviderType() != null) {
			entity.setCreditProviderType(TypesUtil.getEnumForDatabaseValue(stub.getCreditProviderType(), CreditProviderType.class))
		}
		if (stub.getFeeHelpStatus() != null) {
			entity.setFeeHelpStatus(TypesUtil.getEnumForDatabaseValue(stub.getFeeHelpStatus(), EnrolmentVETFeeHelpStatus.class))
		}
		if (stub.getFeeHelpAmount() != null) {
			entity.setFeeHelpAmount(new Money(stub.getFeeHelpAmount()))
		}
		if (stub.getConfirmationStatus() != null) {
			entity.setConfirmationStatus(TypesUtil.getEnumForDatabaseValue(stub.getConfirmationStatus(), ConfirmationStatus.class))
		}
	}
}
