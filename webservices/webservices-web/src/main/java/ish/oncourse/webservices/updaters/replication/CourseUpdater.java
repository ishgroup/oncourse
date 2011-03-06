package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.Qualification;
import ish.oncourse.webservices.v4.stubs.replication.CourseStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class CourseUpdater extends AbstractWillowUpdater<CourseStub, Course> {
	
	public CourseUpdater(College college, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, next);
	}
	
	@Override
	protected void updateEntity(CourseStub stub, Course entity, List<ReplicatedRecord> relationStubs) {
		
		entity.setAllowWaitingList(stub.isAllowWaitingList());
		entity.setAngelId(stub.getAngelId());
		entity.setCode(stub.getCode());
		entity.setCollege(college);
		entity.setCreated(stub.getCreated());
		entity.setDetail(stub.getDetail());
		entity.setDetailTextile(stub.getDetailTextile());
		entity.setFieldOfEducation(stub.getFieldOfEducation());
		entity.setIsSufficientForQualification(stub.isSufficientForQualification());
		entity.setIsVETCourse(stub.isVETCourse());
		entity.setIsWebVisible(stub.isWebVisible());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
				
		entity.setQualification((Qualification) updateRelatedEntity(stub, relationStubs));
		entity.setSearchText(stub.getSearchText());
	}
}
