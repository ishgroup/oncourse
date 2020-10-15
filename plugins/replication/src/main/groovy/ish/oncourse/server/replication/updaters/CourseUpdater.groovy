/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.common.types.CourseEnrolmentType
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.server.cayenne.Qualification
import ish.oncourse.server.reference.ReferenceUtil
import ish.oncourse.webservices.v21.stubs.replication.CourseStub

/**
 */
class CourseUpdater extends AbstractAngelUpdater<CourseStub, Course> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(CourseStub stub, Course entity, RelationShipCallback callback) {
		entity.setAllowWaitingLists(stub.isAllowWaitingList())
		entity.setCode(stub.getCode())
		entity.setCreatedOn(stub.getCreated())
		entity.setFieldOfEducation(stub.getFieldOfEducation())
		entity.setIsSufficientForQualification(stub.isSufficientForQualification())
		entity.setIsVET(stub.isVETCourse())
		entity.setWebDescription(stub.getDetailTextile())
		entity.setModifiedOn(stub.getModified())
		entity.setName(stub.getName())
		Qualification q = null
		def qualificationId = stub.getQualificationId()
		if (qualificationId != null) {
			q = ReferenceUtil.findQualificationByWillowId(entity.getObjectContext(), qualificationId)
		}
		entity.setQualification(q)
		entity.setEnrolmentType(TypesUtil.getEnumForDatabaseValue(stub.getEnrolmentType(), CourseEnrolmentType.class))
		entity.setFieldConfigurationSchema(callback.updateRelationShip(stub.getFieldConfigurationSchemeId(), FieldConfigurationScheme.class))
	}
}
