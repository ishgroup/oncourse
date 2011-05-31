package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Course;
import ish.oncourse.model.Student;
import ish.oncourse.model.WaitingList;
import ish.oncourse.webservices.v4.stubs.replication.WaitingListStub;

public class WaitingListUpdater extends AbstractWillowUpdater<WaitingListStub, WaitingList> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater#
	 * updateEntity
	 * (ish.oncourse.webservices.v4.stubs.replication.ReplicationStub,
	 * ish.oncourse.model.Queueable,
	 * ish.oncourse.webservices.replication.updaters.RelationShipCallback)
	 */
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
