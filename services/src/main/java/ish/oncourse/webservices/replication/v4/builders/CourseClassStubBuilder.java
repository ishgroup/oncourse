package ish.oncourse.webservices.replication.v4.builders;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassStub;

public class CourseClassStubBuilder extends AbstractWillowStubBuilder<CourseClass, CourseClassStub> {

	@Override
	protected CourseClassStub createFullStub(CourseClass entity) {
		CourseClassStub c = new CourseClassStub();
		
		c.setCancelled(entity.isCancelled());
		c.setCode(entity.getCode());
		c.setCountOfSessions(entity.getCountOfSessions());
		c.setCourseId(entity.getCourse().getId());
		c.setCreated(entity.getCreated());
		c.setDeliveryMode(entity.getDeliveryMode());
		c.setDetail(entity.getDetail());
		c.setDetailTextile(entity.getDetailTextile());
		c.setEndDate(entity.getEndDate());
		c.setFeeExGst(entity.getFeeExGst().toBigDecimal());
		c.setFeeGst(entity.getFeeGst().toBigDecimal());
		c.setMaterials(entity.getMaterials());
		c.setMaximumPlaces(entity.getMaximumPlaces());
		c.setMinimumPlaces(entity.getMinimumPlaces());
		c.setMinutesPerSession(entity.getMinutesPerSession());
		Room room = entity.getRoom();
		if (room != null) {
			c.setRoomId(room.getId());
		}
		c.setSessionDetail(entity.getSessionDetail());
		c.setWebVisible(entity.getIsWebVisible());
		
		return c;
	}
}
