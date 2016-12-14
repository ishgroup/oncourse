package ish.oncourse.webservices.replication.v10.updaters;

import ish.common.types.CourseClassAttendanceType;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v10.stubs.replication.CourseClassStub;

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
		entity.setDetail(stub.getDetailTextile() == null ? null : textileConverter.convertCoreTextile(stub.getDetailTextile()));
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
		entity.setIsDistantLearningCourse(stub.isDistantLearningCourse());
		entity.setMaximumDays(stub.getMaximumDays());
		entity.setExpectedHours(stub.getExpectedHours());
		entity.setFeeHelpClass(Boolean.TRUE.equals(stub.isFeeHelpClass()));
		if (stub.getAttendanceType() != null) {
			entity.setAttendanceType(TypesUtil.getEnumForDatabaseValue(stub.getAttendanceType(), CourseClassAttendanceType.class));
		}
		entity.setReportingPeriod(stub.getReportingPeriod());
		entity.setCensusDate(stub.getCensusDate());
		entity.setFullTimeLoad(stub.getFullTimeLoad());
		entity.setMinStudentAge(stub.getMinStudentAge());
		entity.setMaxStudentAge(stub.getMaxStudentAge());
		entity.setIsActive(stub.isActive());
	}
}
