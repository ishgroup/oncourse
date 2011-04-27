package ish.oncourse.webservices.replication.updaters;

import ish.math.Money;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassStub;

public class CourseClassUpdater extends AbstractWillowUpdater<CourseClassStub, CourseClass> {

	@Override
	protected void updateEntity(CourseClassStub stub, CourseClass entity, RelationShipCallback callback) {
			
		entity.setCancelled(stub.isCancelled());
		entity.setCode(stub.getCode());

		entity.setCountOfSessions(stub.getCountOfSessions());

		entity.setCourse(callback.updateRelationShip(stub.getCourseId(), Course.class));

		entity.setCreated(stub.getCreated());
		entity.setDeliveryMode(stub.getDeliveryMode());
		entity.setDetail(stub.getDetail());
		entity.setDetailTextile(stub.getDetailTextile());
		entity.setEndDate(stub.getEndDate());
		entity.setFeeExGst(new Money(stub.getFeeExGst()));
		entity.setFeeGst(new Money(stub.getFeeGst()));
		entity.setIsWebVisible(stub.isWebVisible());
		entity.setMaterials(stub.getMaterials());
		entity.setMaterialsTextile(stub.getMaterialsTextile());
		entity.setMaximumPlaces(stub.getMaximumPlaces());
		entity.setMinimumPlaces(stub.getMinimumPlaces());
		entity.setMinutesPerSession(stub.getMinutesPerSession());
		entity.setModified(stub.getModified());

		entity.setRoom(callback.updateRelationShip(stub.getRoomId(), Room.class));

		entity.setSessionDetail(stub.getSessionDetail());
		entity.setSessionDetailTextile(stub.getSessionDetailTextile());
		entity.setStartDate(stub.getStartDate());
		entity.setStartingMinutePerSession(stub.getStartingMinutePerSession());
		entity.setTimeZone(stub.getTimeZone());
	}
}
