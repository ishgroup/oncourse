package ish.oncourse.webservices.replication.v6.builders;

import ish.common.types.*;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.StudentStub;

public class StudentStubBuilder extends AbstractWillowStubBuilder<Student, StudentStub> {

	@Override
	protected StudentStub createFullStub(Student entity) {
		StudentStub stub = new StudentStub();
		stub.setConcessionType(entity.getConcessionType());
		Contact contact = entity.getContact();
		if (contact != null) {
			stub.setContactId(contact.getId());
		}
		if (entity.getCountryOfBirth() != null) {
			stub.setCountryOfBirthId(entity.getCountryOfBirth().getId());
		}
		stub.setCreated(entity.getCreated());
		if (entity.getDisabilityType() != null) {
			stub.setDisabilityType(entity.getDisabilityType().getDatabaseValue());
		} else {
			stub.setDisabilityType(AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION.getDatabaseValue());
		}
		if (entity.getEnglishProficiency() != null) {
			stub.setEnglishProficiency(entity.getEnglishProficiency().getDatabaseValue());
		} else {
			stub.setEnglishProficiency(AvetmissStudentEnglishProficiency.DEFAULT_POPUP_OPTION.getDatabaseValue());
		}
		if (entity.getHighestSchoolLevel() != null) {
			stub.setHighestSchoolLevel(entity.getHighestSchoolLevel().getDatabaseValue());
		} else {
			stub.setHighestSchoolLevel(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION.getDatabaseValue());
		}
		if (entity.getIndigenousStatus() != null) {
			stub.setIndigenousStatus(entity.getIndigenousStatus().getDatabaseValue());
		} else {
			stub.setIndigenousStatus(AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION.getDatabaseValue());
		}
		stub.setLabourForceType(entity.getLabourForceType());
		stub.setLanguageHomeId((entity.getLanguageHome() != null) ? entity.getLanguageHome().getId() : null);
		stub.setLanguageId((entity.getLanguage() != null) ? entity.getLanguage().getId() : null);
		stub.setModified(entity.getModified());
		boolean isOverseasClient = entity.getIsOverseasClient() != null ? entity.getIsOverseasClient() : false;
		stub.setOverseasClient(isOverseasClient);
		if (entity.getPriorEducationCode() != null) {
			stub.setPriorEducationCode(entity.getPriorEducationCode().getDatabaseValue());
		} else {
			stub.setPriorEducationCode(AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION.getDatabaseValue());
		}
		stub.setStillAtSchool(entity.getIsStillAtSchool());
		stub.setYearSchoolCompleted(entity.getYearSchoolCompleted());
		stub.setChessn(entity.getChessn());
		stub.setFeeHelpEligible(Boolean.TRUE.equals(entity.getFeeHelpEligible()));
		//SpecialNeedsAssistance field is not uses in versions before angel 5.1
		//stub.setSpecialNeedsAssistance(entity.getSpecialNeedsAssistance());
		if (entity.getCitizenship() != null) {
			stub.setCitizenship(entity.getCitizenship().getDatabaseValue());
		} else {
			stub.setCitizenship(StudentCitizenship.NO_INFORMATION.getDatabaseValue());
		}
		return stub;
	}
}
