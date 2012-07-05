package ish.oncourse.webservices.replication.v4.updaters;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseModule;
import ish.oncourse.model.Module;
import ish.oncourse.webservices.v4.stubs.replication.CourseModuleStub;

import org.apache.cayenne.Cayenne;

public class CourseModuleUpdater extends AbstractWillowUpdater<CourseModuleStub, CourseModule> {

	@Override
	protected void updateEntity(CourseModuleStub stub, CourseModule entity, RelationShipCallback callback) {
		
		entity.setCourse(callback.updateRelationShip(stub.getCourseId(), Course.class));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());

		if (stub.getModuleId() != null) {
			entity.setModule(Cayenne.objectForPK(entity.getObjectContext(), Module.class, stub.getModuleId()));
		}
	}
}
