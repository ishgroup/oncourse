package ish.oncourse.webservices.replication.v19.updaters;

import ish.oncourse.model.AssessmentClass;
import ish.oncourse.model.AssessmentClassModule;
import ish.oncourse.model.Module;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v19.stubs.replication.AssessmentClassModuleStub;
import org.apache.cayenne.Cayenne;

/**
 * Created by anarut on 11/30/16.
 */
public class AssessmentClassModuleUpdater extends AbstractWillowUpdater<AssessmentClassModuleStub, AssessmentClassModule> {
	
	@Override
	protected void updateEntity(AssessmentClassModuleStub stub, AssessmentClassModule entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setAssessmentClass(callback.updateRelationShip(stub.getAssessmentClassId(), AssessmentClass.class));
		entity.setModule(Cayenne.objectForPK(entity.getObjectContext(), Module.class, stub.getModuleId()));
	}
}
