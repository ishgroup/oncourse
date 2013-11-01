package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.CourseClassStub;

public class CourseClassStubBuilder extends AbstractWillowStubBuilder<CourseClass, CourseClassStub> {

	@Override
	protected CourseClassStub createFullStub(CourseClass entity) {
		CourseClassStub stub = new CourseClassStub();
		stub.setCancelled(entity.isCancelled());
		stub.setCode(entity.getCode());
		stub.setCountOfSessions(entity.getCountOfSessions());
		stub.setCourseId(entity.getCourse().getId());
		stub.setCreated(entity.getCreated());
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
		return stub;
	}
}
