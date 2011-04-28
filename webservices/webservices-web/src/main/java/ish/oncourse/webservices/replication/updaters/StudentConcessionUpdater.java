package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.webservices.v4.stubs.replication.StudentConcessionStub;

public class StudentConcessionUpdater extends AbstractWillowUpdater< StudentConcessionStub,  StudentConcession> {

	/* (non-Javadoc)
	 * @see ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater#updateEntity(ish.oncourse.webservices.v4.stubs.replication.ReplicationStub, ish.oncourse.model.Queueable, ish.oncourse.webservices.replication.updaters.RelationShipCallback)
	 */
	@Override
	protected void updateEntity(StudentConcessionStub stub, StudentConcession e, RelationShipCallback callback) {
		e.setAuthorisationExpiresOn(stub.getAuthorisationExpiresOn());
		e.setAuthorisedOn(stub.getAuthorisedOn());
		e.setConcessionNumber(stub.getConcessionNumber());
		e.setConcessionType(callback.updateRelationShip(stub.getConcessionTypeId(), ConcessionType.class));
		e.setCreated(stub.getCreated());
		e.setExpiresOn(stub.getExpiresOn());
		e.setModified(stub.getModified());
		e.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
		e.setTimeZone(stub.getTimeZone());
	}
}
