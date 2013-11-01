package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.Course;
import ish.oncourse.model.Qualification;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.CourseStub;
import org.apache.cayenne.Cayenne;

public class CourseUpdater extends AbstractWillowUpdater<CourseStub, Course> {

	private ITextileConverter textileConverter;

	public CourseUpdater(ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(CourseStub stub, Course entity, RelationShipCallback callback) {
		entity.setAllowWaitingList(stub.isAllowWaitingList());
		entity.setCode(stub.getCode());
		entity.setCreated(stub.getCreated());
		if (stub.getDetailTextile() != null) {
			entity.setDetail(textileConverter.convertCoreTextile(stub.getDetailTextile()));
		}
		entity.setDetailTextile(stub.getDetailTextile());
		entity.setFieldOfEducation(stub.getFieldOfEducation());
		entity.setIsSufficientForQualification(stub.isSufficientForQualification());
		entity.setIsVETCourse(stub.isVETCourse());
		entity.setIsWebVisible(stub.isWebVisible());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		if (stub.getQualificationId() != null) {
			entity.setQualification(Cayenne.objectForPK(entity.getObjectContext(), Qualification.class, stub.getQualificationId()));
		}
		if (stub.getNominalHours() != null) {
			entity.setNominalHours(stub.getNominalHours().floatValue());
		}
		entity.setSearchText(stub.getSearchText());
	}
}
