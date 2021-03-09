/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.webservices.v23.stubs.replication.CourseClassStub
import ish.util.LocalDateUtils

import java.util.Date

/**
 */
class CourseClassStubBuilder extends AbstractAngelStubBuilder<CourseClass, CourseClassStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected CourseClassStub createFullStub(CourseClass entity) {
		def stub = new CourseClassStub()
		stub.setCancelled(entity.getIsCancelled())
		stub.setCode(entity.getCode())
		stub.setCountOfSessions(entity.getSessionsCount())
		stub.setCourseId(entity.getCourse().getId())
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setDeliveryMode(entity.getDeliveryMode().getDatabaseValue())
		stub.setEndDate(entity.getEndDateTime())
		stub.setFeeExGst(entity.getFeeExGst().toBigDecimal())
		stub.setFeeGst(entity.getFeeGST().toBigDecimal())
		stub.setMaterials(entity.getMaterials())
		stub.setMaximumPlaces(entity.getMaximumPlaces())
		stub.setMinimumPlaces(entity.getMinimumPlaces())
		stub.setWebVisible(entity.getIsShownOnWeb())
		stub.setDetailTextile(entity.getWebDescription())
		if (entity.getRoom() != null) {
			stub.setRoomId(entity.getRoom().getId())
		}
		stub.setStartDate(entity.getStartDateTime())
		stub.setDistantLearningCourse(entity.getIsDistantLearningCourse())
		stub.setExpectedHours(entity.getExpectedHours())
		stub.setMaximumDays(entity.getMaximumDays())
		def censusDate = LocalDateUtils.valueToDateAtNoon(entity.getCensusDate())
		stub.setCensusDate(censusDate)
		stub.setReportingPeriod(entity.getReportingPeriod())
		stub.setAttendanceType(entity.getAttendanceType().getDatabaseValue())
		stub.setMinStudentAge(entity.getMinStudentAge())
		stub.setMaxStudentAge(entity.getMaxStudentAge())
		stub.setActive(entity.getIsActive())
		if (entity.getFundingSource() != null) {
			stub.setFundingSource(entity.getFundingSource().getDatabaseValue())
		}
		return stub
	}
}
