package ish.oncourse.webservices.replication.v4.updaters;

import ish.math.Money;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassStub;

public class CourseClassUpdater extends AbstractWillowUpdater<CourseClassStub, CourseClass> {

	private ITextileConverter textileConverter;
	
	public CourseClassUpdater(ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
	}
	@Override
	protected void updateEntity(CourseClassStub stub, CourseClass entity, RelationShipCallback callback) {
			
		entity.setCancelled(stub.isCancelled());
		entity.setCode(stub.getCode());

		entity.setCountOfSessions(stub.getCountOfSessions());

		entity.setCourse(callback.updateRelationShip(stub.getCourseId(), Course.class));

		entity.setCreated(stub.getCreated());
		entity.setDeliveryMode(stub.getDeliveryMode());
		if (stub.getDetailTextile() != null) {
			entity.setDetail(textileConverter.convertCoreTextile(stub.getDetailTextile()));
		}
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

		entity.setRoom((stub.getRoomId() != null) ? callback.updateRelationShip(stub.getRoomId(), Room.class) : null);

		entity.setSessionDetail(stub.getSessionDetail());
		entity.setSessionDetailTextile(stub.getSessionDetailTextile());
		entity.setStartDate(stub.getStartDate());
		entity.setStartingMinutePerSession(stub.getStartingMinutePerSession());
		entity.setTimeZone(stub.getTimeZone());
		
		// not used in v4 stubs, setting default value
		entity.setIsDistantLearningCourse(Boolean.FALSE);

		/** 
		 * "isActive" field is not nullable on willow db started by v7 replication.	
		 * v4 stubs is not contains this field.	
		 * We set on false it manually here that not violate db constraint.
		 */
		
		entity.setIsActive(false);
	}
}
