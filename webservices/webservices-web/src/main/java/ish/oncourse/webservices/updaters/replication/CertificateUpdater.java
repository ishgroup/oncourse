package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Certificate;
import ish.oncourse.model.College;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.CertificateStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

import org.apache.cayenne.DataObjectUtils;

public class CertificateUpdater extends AbstractWillowUpdater<CertificateStub, Certificate> {
	
	public CertificateUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}

	@Override
	protected void updateEntity(CertificateStub stub, Certificate entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setCertificateNumber(stub.getCertificateNumber());
		entity.setCollege(getCollege(entity.getObjectContext()));
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
			entity.setQualification(DataObjectUtils.objectForPK(entity.getObjectContext(), Qualification.class, qualificationId));
		}
		
		entity.setRevokedWhen(stub.getRevokedWhen());
		entity.setStudent((Student) updateRelatedEntity(entity.getObjectContext(), stub.getStudent(), relationStubs));
		entity.setStudentFirstName(stub.getStudentFirstName());
		entity.setStudentLastName(stub.getStudentLastName());
	}
}
