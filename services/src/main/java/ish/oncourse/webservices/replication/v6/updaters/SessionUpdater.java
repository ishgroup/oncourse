package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.SessionStub;

public class SessionUpdater extends AbstractWillowUpdater<SessionStub, Session> {

	@Override
	protected void updateEntity(SessionStub stub, Session entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class));
		entity.setEndDate(stub.getEndDate());
		entity.setMarker((stub.getMarkerId() != null) ? callback.updateRelationShip(stub.getMarkerId(), Tutor.class) : null);
		entity.setRoom((stub.getRoomId() != null) ? callback.updateRelationShip(stub.getRoomId(), Room.class) : null);
		entity.setStartDate(stub.getStartDate());

	}

}
