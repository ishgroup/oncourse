package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.Course;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.CourseStub;

import java.math.BigDecimal;

public class CourseStubBuilder extends AbstractWillowStubBuilder<Course, CourseStub> {

	@Override
	protected CourseStub createFullStub(Course entity) {
		CourseStub stub = new CourseStub();
		stub.setCode(entity.getCode());
		stub.setCreated(entity.getCreated());
		stub.setDetail(entity.getDetail());
		stub.setDetailTextile(entity.getDetailTextile());
		stub.setFieldOfEducation(entity.getFieldOfEducation());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		if (entity.getNominalHours() != null) {
			stub.setNominalHours(new BigDecimal(entity.getNominalHours()));
		}
		stub.setQualificationId((entity.getQualification() != null) ? entity
				.getQualification().getId() : null);
		stub.setSearchText(entity.getSearchText());
		stub.setSufficientForQualification(entity.getIsSufficientForQualification());
		stub.setAllowWaitingList(entity.getAllowWaitingList());
		stub.setVETCourse(entity.getIsVETCourse());
		stub.setWebVisible(entity.getIsWebVisible());
		return stub;
	}
}
