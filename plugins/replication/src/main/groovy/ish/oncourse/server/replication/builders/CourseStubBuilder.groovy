/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Course
import ish.oncourse.webservices.v21.stubs.replication.CourseStub

/**
 */
class CourseStubBuilder extends AbstractAngelStubBuilder<Course, CourseStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected CourseStub createFullStub(Course entity) {
		def stub = new CourseStub()
		stub.setAllowWaitingList(entity.getAllowWaitingLists())
		stub.setCode(entity.getCode())
		stub.setCreated(entity.getCreatedOn())
		stub.setDetailTextile(entity.getWebDescription())
		stub.setFieldOfEducation(entity.getFieldOfEducation())
		stub.setModified(entity.getModifiedOn())
		stub.setName(entity.getName())
		if (entity.getQualification() != null) {
			stub.setQualificationId(entity.getQualification().getWillowId())
		}
		stub.setSufficientForQualification(entity.getIsSufficientForQualification())
		stub.setVETCourse(entity.getIsVET())
		stub.setWebVisible(entity.getIsShownOnWeb())
		stub.setNominalHours(entity.getReportableHours())
		stub.setEnrolmentType(entity.getEnrolmentType().getDatabaseValue())
		if (entity.getFieldConfigurationSchema() != null) {
			stub.setFieldConfigurationSchemeId(entity.getFieldConfigurationSchema().getId())
		}
		return stub
	}
}
