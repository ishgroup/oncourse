package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.v4.stubs.replication.StudentStub;

import java.util.Map;

public class StudentStubBuilder extends AbstractWillowStubBuilder<Student, StudentStub> {
	
	public StudentStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowStubBuilder next) {
		super(queue, next);
	}

	@Override
	protected StudentStub createFullStub(Student entity) {
		StudentStub stub = new StudentStub();
		
		stub.setWillowId(entity.getId());
		stub.setConcessionType(entity.getConcessionType());
		stub.setContact(findRelatedStub(entity.getContact()));
		stub.setCountryOfBirthId(entity.getCountryOfBirth().getId());
		stub.setCreated(entity.getCreated());
		stub.setDisabilityType(entity.getDisabilityType().getDatabaseValue());
		stub.setEnglishProficiency(entity.getEnglishProficiency().getDatabaseValue());
		stub.setHighestSchoolLevel(entity.getHighestSchoolLevel().getDatabaseValue());
		stub.setIndigenousStatus(entity.getIndigenousStatus().getDatabaseValue());
		stub.setLabourForceType(entity.getLabourForceType());
		stub.setLanguageHomeId(entity.getLanguageHome().getId());
		stub.setLanguageId(entity.getLanguageId());
		stub.setModified(entity.getModified());
		stub.setOverseasClient(entity.getIsOverseasClient());
		stub.setPriorEducationCode(entity.getPriorEducationCode().getDatabaseValue());
		stub.setStillAtSchool(entity.getIsStillAtSchool());
		stub.setYearSchoolCompleted(entity.getYearSchoolCompleted());
		
		return stub;
	}
}
