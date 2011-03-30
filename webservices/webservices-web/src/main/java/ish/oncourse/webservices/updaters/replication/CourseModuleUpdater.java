package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseModule;
import ish.oncourse.model.Module;
import ish.oncourse.webservices.v4.stubs.replication.CourseModuleStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

import org.apache.cayenne.Cayenne;

public class CourseModuleUpdater extends AbstractWillowUpdater<CourseModuleStub, CourseModule> {

	@SuppressWarnings("unchecked")
	@Override
	protected void updateEntity(CourseModuleStub stub, CourseModule entity, List<ReplicatedRecord> result) {
		
		entity.setAngelId(stub.getAngelId());
		entity.setCourse((Course) updateRelationShip(stub.getCourseId(), "Course", result));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());

		if (stub.getModuleId() != null) {
			entity.setModule(Cayenne.objectForPK(entity.getObjectContext(), Module.class, stub.getModuleId()));
		}
	}
}
