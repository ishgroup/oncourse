package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Student;
import ish.oncourse.webservices.v4.stubs.replication.StudentStub;

public class StudentStubBuilder extends AbstractWillowStubBuilder<Student, StudentStub> {

	@Override
	protected StudentStub createFullStub(Student entity) {
		StudentStub stub = new StudentStub();

		stub.setConcessionType(entity.getConcessionType());
		stub.setContactId(entity.getContact().getId());
		stub.setCountryOfBirthId(entity.getCountryOfBirth().getId());
		stub.setCreated(entity.getCreated());
		stub.setDisabilityType(entity.getDisabilityType().getDatabaseValue());
		stub.setEnglishProficiency(entity.getEnglishProficiency().getDatabaseValue());
		stub.setHighestSchoolLevel(entity.getHighestSchoolLevel().getDatabaseValue());
		stub.setIndigenousStatus(entity.getIndigenousStatus().getDatabaseValue());
		stub.setLabourForceType(entity.getLabourForceType());
		stub.setLanguageHomeId(entity.getLanguageHome().getId());
		stub.setLanguageId(entity.getLanguage().getId());
		stub.setModified(entity.getModified());
		stub.setOverseasClient(entity.getIsOverseasClient());
		stub.setPriorEducationCode(entity.getPriorEducationCode().getDatabaseValue());
		stub.setStillAtSchool(entity.getIsStillAtSchool());
		stub.setYearSchoolCompleted(entity.getYearSchoolCompleted());

		return stub;
	}
}
