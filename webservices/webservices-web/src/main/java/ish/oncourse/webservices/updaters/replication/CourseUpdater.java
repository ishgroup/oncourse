package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Course;
import ish.oncourse.model.Qualification;
import ish.oncourse.webservices.v4.stubs.replication.CourseStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

import org.apache.cayenne.Cayenne;

public class CourseUpdater extends AbstractWillowUpdater<CourseStub, Course> {
	
	@Override
	protected void updateEntity(CourseStub stub, Course entity, List<ReplicatedRecord> result) {
		
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
		
		Long qualificationId = stub.getQualificationId();
		
		if (qualificationId != null) {
			Qualification q = Cayenne.objectForPK(entity.getObjectContext(), Qualification.class, qualificationId);
			entity.setQualification(q);
		}
		
		entity.setSearchText(stub.getSearchText());
	}
}
