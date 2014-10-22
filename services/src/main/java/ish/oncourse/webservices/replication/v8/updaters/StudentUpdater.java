package ish.oncourse.webservices.replication.v8.updaters;

import ish.common.types.*;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v4.updaters.UpdaterException;
import ish.oncourse.webservices.v8.stubs.replication.StudentStub;
import org.apache.cayenne.Cayenne;

public class StudentUpdater extends AbstractWillowUpdater<StudentStub, Student> {

	@Override
	protected void updateEntity(StudentStub stub, Student entity, RelationShipCallback callback) {	
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		if (stub.getContactId() != null) {
			entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		} else {
			final String message = String.format("Student with angelId = %s without linked contact detected!", stub.getAngelId());
            throw new UpdaterException(message);
		}
		entity.setConcessionType(stub.getConcessionType());
		if (stub.getCountryOfBirthId() != null) {
			entity.setCountryOfBirth(Cayenne.objectForPK(entity.getObjectContext(), Country.class, stub.getCountryOfBirthId()));
		}
		entity.setDisabilityType(TypesUtil.getEnumForDatabaseValue(stub.getDisabilityType(), AvetmissStudentDisabilityType.class));
		entity.setEnglishProficiency(TypesUtil.getEnumForDatabaseValue(stub.getEnglishProficiency(), AvetmissStudentEnglishProficiency.class));
		entity.setHighestSchoolLevel(TypesUtil.getEnumForDatabaseValue(stub.getHighestSchoolLevel(), AvetmissStudentSchoolLevel.class));
		entity.setIndigenousStatus(TypesUtil.getEnumForDatabaseValue(stub.getIndigenousStatus(), AvetmissStudentIndigenousStatus.class));
		entity.setIsOverseasClient(stub.isOverseasClient());
		entity.setIsStillAtSchool(stub.isStillAtSchool());
		entity.setLabourForceType(stub.getLabourForceType());
		if (stub.getLanguageId() != null) {
			entity.setLanguage(Cayenne.objectForPK(entity.getObjectContext(), Language.class, stub.getLanguageId()));
		}
		if (stub.getLanguageHomeId() != null) {
			entity.setLanguageHome(Cayenne.objectForPK(entity.getObjectContext(), Language.class, stub.getLanguageHomeId()));
		}
		entity.setPriorEducationCode(TypesUtil.getEnumForDatabaseValue(stub.getPriorEducationCode(), AvetmissStudentPriorEducation.class));
		entity.setYearSchoolCompleted(stub.getYearSchoolCompleted());
		entity.setChessn(stub.getChessn());
		entity.setFeeHelpEligible(Boolean.TRUE.equals(stub.isFeeHelpEligible()));
		entity.setSpecialNeedsAssistance(stub.isSpecialNeedsAssistance());
		if (stub.getCitizenship() != null) {
			entity.setCitizenship(TypesUtil.getEnumForDatabaseValue(stub.getCitizenship(), StudentCitizenship.class));
		}
		entity.setSpecialNeeds(stub.getSpecialNeeds());
		entity.setTownOfBirth(stub.getTownOfBirth());
		entity.setUsi(stub.getUsi());
		if (stub.getUsiStatus() != null) {
			entity.setUsiStatus(TypesUtil.getEnumForDatabaseValue(stub.getUsiStatus(),UsiStatus.class ));
		}
	}

}
