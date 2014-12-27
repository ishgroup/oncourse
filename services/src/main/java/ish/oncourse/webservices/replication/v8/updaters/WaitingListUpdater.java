package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.Course;
import ish.oncourse.model.Student;
import ish.oncourse.model.WaitingList;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.WaitingListStub;

public class WaitingListUpdater extends AbstractWillowUpdater<WaitingListStub, WaitingList> {

	@Override
	protected void updateEntity(WaitingListStub stub, WaitingList entity, RelationShipCallback callback) {
		entity.setCourse(callback.updateRelationShip(stub.getCourseId(), Course.class));
		entity.setCreated(stub.getCreated());
		entity.setDetail(stub.getDetail());
		entity.setModified(stub.getModified());
		entity.setPotentialStudents(stub.getStudentCount());
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
	}
}
