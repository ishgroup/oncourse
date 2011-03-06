package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassStub;

import java.util.Map;

public class CourseClassStubBuilder extends AbstractWillowStubBuilder<CourseClass, CourseClassStub> {
	
	public CourseClassStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowQueueService queueService, IWillowStubBuilder next) {
		super(queue, queueService, next);
	}

	@Override
	protected CourseClassStub createFullStub(CourseClass entity) {
		CourseClassStub c = new CourseClassStub();
		
		c.setWillowId(entity.getId());
		c.setAngelId(entity.getAngelId());
		c.setCancelled(entity.isCancelled());
		c.setCode(entity.getCode());
		c.setCountOfSessions(entity.getCountOfSessions());
		c.setCourse(findRelationshipStub(entity.getCourse()));
		c.setCreated(entity.getCreated());
		c.setDeliveryMode(entity.getDeliveryMode());
		c.setDetail(entity.getDetail());
		c.setEndDate(entity.getEndDate());
		c.setFeeExGst(entity.getFeeExGst().toBigDecimal());
		c.setFeeGst(entity.getFeeGst().toBigDecimal());
		c.setMaterials(entity.getMaterials());
		c.setMaximumPlaces(entity.getMaximumPlaces());
		c.setMinimumPlaces(entity.getMinimumPlaces());
		c.setMinutesPerSession(entity.getMinutesPerSession());
		c.setRoom(findRelationshipStub(entity.getRoom()));
		c.setSessionDetail(entity.getSessionDetail());
		c.setWebVisible(entity.getIsWebVisible());
		
		return c;
	}
}
