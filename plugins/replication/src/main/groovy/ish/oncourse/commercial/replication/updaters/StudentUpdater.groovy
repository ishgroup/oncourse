/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.common.types.*
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Language
import ish.oncourse.server.cayenne.Student
import ish.oncourse.commercial.replication.reference.ReferenceUtil
import ish.oncourse.webservices.v23.stubs.replication.StudentStub

/**
 */
class StudentUpdater extends AbstractAngelUpdater<StudentStub, Student> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(StudentStub stub, Student entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		def contactId = stub.getContactId()

		def contact = callback.updateRelationShip(contactId, Contact.class)

		if (contact == null) {
			final def message = String.format("Failed to process Student: willowId=%d, related Contact: willowId=%d does not exist on angel", stub.getWillowId(), stub.getContactId())
			throw new IllegalArgumentException(message)
		} else {
			entity.setContact(contact)
		}

		Country country = null
		def countryId = stub.getCountryOfBirthId()
		if (countryId != null) {
			country = ReferenceUtil.findCountryByWillowId(entity.getObjectContext(), countryId)
		}
		entity.setCountryOfBirth(country)
		entity.setDisabilityType(TypesUtil.getEnumForDatabaseValue(stub.getDisabilityType(), AvetmissStudentDisabilityType.class))
		entity.setEnglishProficiency(TypesUtil.getEnumForDatabaseValue(stub.getEnglishProficiency(), AvetmissStudentEnglishProficiency.class))
		entity.setHighestSchoolLevel(TypesUtil.getEnumForDatabaseValue(stub.getHighestSchoolLevel(), AvetmissStudentSchoolLevel.class))
		entity.setIndigenousStatus(TypesUtil.getEnumForDatabaseValue(stub.getIndigenousStatus(), AvetmissStudentIndigenousStatus.class))
		entity.setIsOverseasClient(stub.isOverseasClient())
		entity.setIsStillAtSchool(stub.isStillAtSchool())
		Language lang = null
		def langId = stub.getLanguageId()
		if (langId != null) {
			lang = ReferenceUtil.findLanguageByWillowId(entity.getObjectContext(), langId)
		}
		entity.setLanguage(lang)
		if (stub.getLabourForceType() != null) {
			entity.setLabourForceStatus(TypesUtil.getEnumForDatabaseValue(stub.getLabourForceType(), AvetmissStudentLabourStatus.class))
		}
		entity.setPriorEducationCode(TypesUtil.getEnumForDatabaseValue(stub.getPriorEducationCode(), AvetmissStudentPriorEducation.class))
		entity.setYearSchoolCompleted(stub.getYearSchoolCompleted())
		entity.setChessn(stub.getChessn())
		entity.setFeeHelpEligible(stub.isFeeHelpEligible())
		entity.setSpecialNeedsAssistance(stub.isSpecialNeedsAssistance())
		if (stub.getCitizenship() != null) {
			entity.setCitizenship(TypesUtil.getEnumForDatabaseValue(stub.getCitizenship(), StudentCitizenship.class))
		}
		entity.setSpecialNeeds(stub.getSpecialNeeds())
		entity.setTownOfBirth(stub.getTownOfBirth())
		entity.setUsi(stub.getUsi())
		if (stub.getUsiStatus() != null) {
			entity.setUsiStatus(TypesUtil.getEnumForDatabaseValue(stub.getUsiStatus(), UsiStatus.class))
		}

	}
}
