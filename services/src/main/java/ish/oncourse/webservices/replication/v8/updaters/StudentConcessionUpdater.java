package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.StudentConcessionStub;

public class StudentConcessionUpdater extends AbstractWillowUpdater< StudentConcessionStub,  StudentConcession> {

	@Override
	protected void updateEntity(StudentConcessionStub stub, StudentConcession entity, RelationShipCallback callback) {
		entity.setAuthorisationExpiresOn(stub.getAuthorisationExpiresOn());
		entity.setAuthorisedOn(stub.getAuthorisedOn());
		entity.setConcessionNumber(stub.getConcessionNumber());
		entity.setConcessionType(callback.updateRelationShip(stub.getConcessionTypeId(), ConcessionType.class));
		entity.setCreated(stub.getCreated());
		entity.setExpiresOn(stub.getExpiresOn());
		entity.setModified(stub.getModified());
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
		entity.setTimeZone(stub.getTimeZone());
	}
}
