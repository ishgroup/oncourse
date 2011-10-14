package ish.oncourse.webservices.replication.builders;

import java.math.BigDecimal;

import ish.oncourse.model.Course;
import ish.oncourse.webservices.v4.stubs.replication.CourseStub;

public class CourseStubBuilder extends
		AbstractWillowStubBuilder<Course, CourseStub> {

	@Override
	protected CourseStub createFullStub(Course entity) {
		CourseStub c = new CourseStub();
		c.setCode(entity.getCode());
		c.setCreated(entity.getCreated());
		c.setDetail(entity.getDetail());
		c.setDetailTextile(entity.getDetailTextile());
		c.setFieldOfEducation(entity.getFieldOfEducation());
		c.setModified(entity.getModified());
		c.setName(entity.getName());
		if (entity.getNominalHours() != null) {
			c.setNominalHours(new BigDecimal(entity.getNominalHours()));
		}
		c.setQualificationId((entity.getQualification() != null) ? entity
				.getQualification().getId() : null);
		c.setSearchText(entity.getSearchText());
		c.setSufficientForQualification(entity.getIsSufficientForQualification());
		c.setAllowWaitingList(entity.getAllowWaitingList());

		c.setVETCourse(entity.getIsVETCourse());
		c.setWebVisible(entity.getIsWebVisible());

		return c;
	}
}
