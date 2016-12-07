package ish.oncourse.webservices.replication.v14.builders;

import ish.oncourse.model.AssessmentClassModule;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v14.stubs.replication.AssessmentClassModuleStub;

/**
 * Created by anarut on 11/30/16.
 */
public class AssessmentClassModuleStubBuilder extends AbstractWillowStubBuilder<AssessmentClassModule, AssessmentClassModuleStub> {
	
	@Override
	protected AssessmentClassModuleStub createFullStub(AssessmentClassModule entity) {
		AssessmentClassModuleStub stub = new AssessmentClassModuleStub();
		stub.setModified(entity.getModified());
		stub.setAssessmentClassId(entity.getAssessmentClass().getId());
		stub.setModuleId(entity.getModule().getId());
		return stub;
	}
}
