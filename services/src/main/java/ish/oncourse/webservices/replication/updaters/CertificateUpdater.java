package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Certificate;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.v4.stubs.replication.CertificateStub;

import org.apache.cayenne.Cayenne;

public class CertificateUpdater extends AbstractWillowUpdater<CertificateStub, Certificate> {

	@Override
	protected void updateEntity(CertificateStub stub, Certificate entity, RelationShipCallback callback) {
		entity.setCertificateNumber(stub.getCertificateNumber());
		entity.setCreated(stub.getCreated());
		entity.setEndDate(stub.getEndDate());
		entity.setFundingSource(stub.getFundingSource());
		entity.setIsQualification(stub.isQualification());
		entity.setModified(entity.getModified());
		entity.setPrintedWhen(entity.getPrintedWhen());
		entity.setPrivateNotes(entity.getPrivateNotes());
		entity.setPublicNotes(entity.getPublicNotes());

		Long qualificationId = stub.getQualificationId();
		if (qualificationId != null) {
			entity.setQualification(Cayenne.objectForPK(entity.getObjectContext(), Qualification.class, qualificationId));
		}

		entity.setRevokedWhen(stub.getRevokedWhen());
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
		entity.setStudentFirstName(stub.getStudentFirstName());
		entity.setStudentLastName(stub.getStudentLastName());
	}
}
