package ish.oncourse.webservices.replication.updaters;

import ish.common.types.AvetmissStudentDisabilityType;
import ish.common.types.AvetmissStudentEnglishProficiency;
import ish.common.types.AvetmissStudentIndigenousStatus;
import ish.common.types.AvetmissStudentPriorEducation;
import ish.common.types.AvetmissStudentSchoolLevel;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.v4.stubs.replication.StudentStub;

import org.apache.cayenne.Cayenne;

public class StudentUpdater extends AbstractWillowUpdater<StudentStub, Student> {

	@Override
	protected void updateEntity(StudentStub stub, Student entity, RelationShipCallback callback) {	
		
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		entity.setConcessionType(stub.getConcessionType());
		
		Long countryId = stub.getCountryOfBirthId();
		if (countryId != null) {
			Country c = Cayenne.objectForPK(entity.getObjectContext(), Country.class, countryId);
			entity.setCountryOfBirth(c);
		}

		entity.setDisabilityType(AvetmissStudentDisabilityType.getEnumForDatabaseValue(stub.getDisabilityType()));
		entity.setEnglishProficiency(AvetmissStudentEnglishProficiency.getEnumForDatabaseValue(stub.getEnglishProficiency()));
		entity.setHighestSchoolLevel(AvetmissStudentSchoolLevel.getEnumForDatabaseValue(stub.getHighestSchoolLevel()));
		entity.setIndigenousStatus(AvetmissStudentIndigenousStatus.getEnumForDatabaseValue(stub.getIndigenousStatus()));
		entity.setIsOverseasClient(stub.isOverseasClient());
		entity.setIsStillAtSchool(stub.isStillAtSchool());
		entity.setLabourForceType(stub.getLabourForceType());
		
		Long languageId = stub.getLanguageId();
		if (languageId != null) {
			Language l = Cayenne.objectForPK(entity.getObjectContext(), Language.class, languageId);
			entity.setLanguage(l);
		}
		
		Long languageHomeId = stub.getLanguageHomeId();
		if (languageHomeId != null) {
			Language l = Cayenne.objectForPK(entity.getObjectContext(), Language.class, languageHomeId);
			entity.setLanguageHome(l);
		}
		
		entity.setPriorEducationCode(AvetmissStudentPriorEducation.getEnumForDatabaseValue(stub.getPriorEducationCode()));
		entity.setYearSchoolCompleted(stub.getYearSchoolCompleted());
	}

}
