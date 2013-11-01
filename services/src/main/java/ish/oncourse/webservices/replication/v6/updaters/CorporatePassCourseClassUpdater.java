package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.CorporatePassCourseClass;
import ish.oncourse.model.CourseClass;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.CorporatePassCourseClassStub;

public class CorporatePassCourseClassUpdater extends AbstractWillowUpdater<CorporatePassCourseClassStub, CorporatePassCourseClass> {

	@Override
	protected void updateEntity(CorporatePassCourseClassStub stub, CorporatePassCourseClass entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setCorporatePass(callback.updateRelationShip(stub.getCorporatePassId(), CorporatePass.class));
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class));
	}

}
