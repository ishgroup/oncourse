package ish.oncourse.webservices.replication.v7.builders;

import ish.common.types.CourseClassAttendanceType;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v7.stubs.replication.CourseClassStub;

public class CourseClassStubBuilder extends AbstractWillowStubBuilder<CourseClass, CourseClassStub> {

	@Override
	protected CourseClassStub createFullStub(CourseClass entity) {
		CourseClassStub stub = new CourseClassStub();
		stub.setCancelled(entity.isCancelled());
		stub.setCode(entity.getCode());
		stub.setCountOfSessions(entity.getCountOfSessions());
		stub.setCourseId(entity.getCourse().getId());
		stub.setModified(entity.getModified());
		stub.setDeliveryMode(entity.getDeliveryMode());
		stub.setDetail(entity.getDetail());
		stub.setDetailTextile(entity.getDetailTextile());
		stub.setEndDate(entity.getEndDate());
		stub.setFeeExGst(entity.getFeeExGst().toBigDecimal());
		stub.setFeeGst(entity.getFeeGst().toBigDecimal());
		stub.setMaterials(entity.getMaterials());
		stub.setMaximumPlaces(entity.getMaximumPlaces());
		stub.setMinimumPlaces(entity.getMinimumPlaces());
		stub.setMinutesPerSession(entity.getMinutesPerSession());
		Room room = entity.getRoom();
		if (room != null) {
			stub.setRoomId(room.getId());
		}
		stub.setSessionDetail(entity.getSessionDetail());
		stub.setWebVisible(entity.getIsWebVisible());
		stub.setDistantLearningCourse(entity.getIsDistantLearningCourse());
		stub.setMaximumDays(entity.getMaximumDays());
		stub.setExpectedHours(entity.getExpectedHours());
		stub.setFeeHelpClass(Boolean.TRUE.equals(entity.getFeeHelpClass()));
		if (entity.getAttendanceType() != null) {
			stub.setAttendanceType(entity.getAttendanceType().getDatabaseValue());
		} else {
			stub.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION.getDatabaseValue());
		}
		stub.setReportingPeriod(entity.getReportingPeriod());
		stub.setCensusDate(entity.getCensusDate());
		stub.setFullTimeLoad(entity.getFullTimeLoad());
		return stub;
	}
}
