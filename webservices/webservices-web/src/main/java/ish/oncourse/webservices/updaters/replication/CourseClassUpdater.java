package ish.oncourse.webservices.updaters.replication;

import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class CourseClassUpdater extends AbstractWillowUpdater<CourseClassStub, CourseClass> {
	
	public CourseClassUpdater(College college, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, next);
	}
	
	@Override
	protected void updateEntity(CourseClassStub stub, CourseClass entity, List<ReplicatedRecord> relationStubs) {
		
		entity.setAngelId(stub.getAngelId());
		entity.setCancelled(stub.isCancelled());
		entity.setCode(stub.getCode());
		
		entity.setCollege(college);
		
		entity.setCountOfSessions(stub.getCountOfSessions());
		
		entity.setCourse((Course) updateRelatedEntity(stub.getCourse(), relationStubs));
		
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
		
		entity.setRoom((Room) updateRelatedEntity(stub.getRoom(), relationStubs));
		
		entity.setSessionDetail(stub.getSessionDetail());
		entity.setSessionDetailTextile(stub.getSessionDetailTextile());
		entity.setStartDate(stub.getStartDate());
		entity.setStartingMinutePerSession(stub.getStartingMinutePerSession());
		entity.setTimeZone(stub.getTimeZone());		
	}
}
