package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.CourseModule;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.CourseModuleStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class CourseModuleUpdater extends AbstractWillowUpdater<CourseModuleStub, CourseModule> {
	
	public CourseModuleUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}

	@Override
	protected void updateEntity(CourseModuleStub stub, CourseModule entity, List<ReplicatedRecord> relationStubs) {
		// TODO Auto-generated method stub
		
	}
}
