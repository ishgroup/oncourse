/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Student
import ish.oncourse.webservices.v21.stubs.replication.StudentStub

/**
 */
class StudentStubBuilder extends AbstractAngelStubBuilder<Student, StudentStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected StudentStub createFullStub(Student s) {
		def stub = new StudentStub()
		stub.setConcessionType(s.getConcessionTypeObsolete())
		if (s.getContact() != null) {
			stub.setContactId(s.getContact().getId())
		}
		if (s.getCountryOfBirth() != null) {
			stub.setCountryOfBirthId(s.getCountryOfBirth().getWillowId())
		}
		stub.setCreated(s.getCreatedOn())
		stub.setDisabilityType(s.getDisabilityType().getDatabaseValue())
		stub.setEnglishProficiency(s.getEnglishProficiency().getDatabaseValue())
		stub.setHighestSchoolLevel(s.getHighestSchoolLevel().getDatabaseValue())
		stub.setIndigenousStatus(s.getIndigenousStatus().getDatabaseValue())
		stub.setLabourForceType(s.getLabourForceStatus().getDatabaseValue())
		if (s.getLanguage() != null) {
			stub.setLanguageId(s.getLanguage().getWillowId())
		}
		stub.setModified(s.getModifiedOn())
		stub.setOverseasClient(s.getIsOverseasClient())
		stub.setPriorEducationCode(s.getPriorEducationCode().getDatabaseValue())
		stub.setStillAtSchool(s.getIsStillAtSchool())
		stub.setYearSchoolCompleted(s.getYearSchoolCompleted())
		stub.setChessn(s.getChessn())
		stub.setFeeHelpEligible(s.getFeeHelpEligible())
		stub.setSpecialNeedsAssistance(s.getSpecialNeedsAssistance())
		stub.setCitizenship(s.getCitizenship().getDatabaseValue())
		stub.setSpecialNeeds(s.getSpecialNeeds())
		stub.setTownOfBirth(s.getTownOfBirth())
		stub.setUsi(s.getUsi())
		if (s.getUsiStatus() != null) {
			stub.setUsiStatus(s.getUsiStatus().getDatabaseValue())
		}
		return stub
	}
}
