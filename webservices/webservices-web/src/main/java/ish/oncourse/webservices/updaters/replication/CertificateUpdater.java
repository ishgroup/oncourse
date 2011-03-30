package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Certificate;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.v4.stubs.replication.CertificateStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

import org.apache.cayenne.Cayenne;

public class CertificateUpdater extends AbstractWillowUpdater<CertificateStub, Certificate> {

	@SuppressWarnings("unchecked")
	@Override
	protected void updateEntity(CertificateStub stub, Certificate entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCertificateNumber(stub.getCertificateNumber());
		entity.setCollege(college);
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
		entity.setStudent((Student) updateRelationShip(stub.getStudentId(), "Student", result));
		entity.setStudentFirstName(stub.getStudentFirstName());
		entity.setStudentLastName(stub.getStudentLastName());
	}
}
