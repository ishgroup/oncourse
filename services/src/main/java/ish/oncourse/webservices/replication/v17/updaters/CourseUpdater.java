package ish.oncourse.webservices.replication.v17.updaters;

import ish.common.types.CourseEnrolmentType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Course;
import ish.oncourse.model.FieldConfigurationScheme;
import ish.oncourse.model.Qualification;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v17.stubs.replication.CourseStub;
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
		entity.setQualification(
				stub.getQualificationId() != null ? Cayenne.objectForPK(entity.getObjectContext(), Qualification.class, stub.getQualificationId()) : null);
		entity.setNominalHours(stub.getNominalHours() != null ? stub.getNominalHours().floatValue() : 0F);
		entity.setSearchText(stub.getSearchText());
		entity.setEnrolmentType(TypesUtil.getEnumForDatabaseValue(stub.getEnrolmentType(), CourseEnrolmentType.class));
		entity.setFieldConfigurationScheme(callback.updateRelationShip(stub.getFieldConfigurationSchemeId(), FieldConfigurationScheme.class));
	}
}
