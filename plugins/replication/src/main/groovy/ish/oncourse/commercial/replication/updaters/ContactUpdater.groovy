/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.common.types.Gender
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tax
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.commercial.replication.reference.ReferenceUtil
import ish.oncourse.webservices.v22.stubs.replication.ContactStub
import ish.util.LocalDateUtils

/**
 */
class ContactUpdater extends AbstractAngelUpdater<ContactStub, Contact> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(ContactStub stub, Contact entity, RelationShipCallback callback) {
		entity.setAllowEmail(stub.isMarketingViaEmailAllowed())
		entity.setAllowPost(stub.isMarketingViaPostAllowed())
		entity.setAllowSms(stub.isMarketingViaSMSAllowed())
		entity.setBirthDate(LocalDateUtils.dateToValue(stub.getDateOfBirth()))
		Country country = null
		def countryId = stub.getCountryId()
		if (countryId != null) {
			country = ReferenceUtil.findCountryByWillowId(entity.getObjectContext(), countryId)
		}
		entity.setCountry(country)
		entity.setCreatedOn(stub.getCreated())
		entity.setEmail(stub.getEmailAddress())
		entity.setLastName(stub.getFamilyName())
		entity.setFax(stub.getFaxNumber())
		entity.setFirstName(stub.getGivenName())
		entity.setHomePhone(stub.getHomePhoneNumber())
		entity.setIsCompany(stub.isCompany())
		entity.setGender(TypesUtil.getEnumForDatabaseValue(stub.getGender(), Gender.class))
		entity.setUniqueCode(stub.getUniqueCode())
		entity.setSuburb(stub.getSuburb())
		entity.setStreet(stub.getStreet())
		entity.setPostcode(stub.getPostcode())
		entity.setWorkPhone(stub.getBusinessPhoneNumber())
		entity.setState(stub.getState())
		entity.setMobilePhone(stub.getMobilePhoneNumber())
		def tutorId = stub.getTutorId()
		if (tutorId != null) {
			entity.setTutor(callback.updateRelationShip(tutorId, Tutor.class))
		}
		def studentId = stub.getStudentId()
		if (studentId != null) {
			entity.setStudent(callback.updateRelationShip(studentId, Student.class))
		}
		entity.setTfn(stub.getTaxFileNumber())
		entity.setMiddleName(stub.getMiddleName())
		entity.setAbn(stub.getAbn())
		entity.setInvoiceTerms(stub.getInvoiceTerms())
		entity.setTitle(stub.getTitle())
		def taxId = stub.getTaxOverrideId()
		if (taxId != null) {
			entity.setTaxOverride(callback.updateRelationShip(taxId, Tax.class))
		}
		entity.setTitle(stub.getTitle())
		entity.setHonorific(stub.getHonorific())
	}
}
