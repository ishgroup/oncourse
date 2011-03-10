package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseModule;
import ish.oncourse.model.Module;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.CourseModuleStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

import org.apache.cayenne.DataObjectUtils;

public class CourseModuleUpdater extends AbstractWillowUpdater<CourseModuleStub, CourseModule> {

	public CourseModuleUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}

	@Override
	protected void updateEntity(CourseModuleStub stub, CourseModule entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setCourse((Course) updateRelatedEntity(entity.getObjectContext(), stub.getCourse(), relationStubs));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());

		if (stub.getModuleId() != null) {
			entity.setModule(DataObjectUtils.objectForPK(entity.getObjectContext(), Module.class, stub.getModuleId()));
		}
	}
}
