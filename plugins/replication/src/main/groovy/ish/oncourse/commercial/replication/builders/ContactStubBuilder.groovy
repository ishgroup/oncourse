/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Contact
import ish.oncourse.webservices.v22.stubs.replication.ContactStub
import ish.util.LocalDateUtils
/**
 */
class ContactStubBuilder extends AbstractAngelStubBuilder<Contact, ContactStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected ContactStub createFullStub(Contact c) {
		def stub = new ContactStub()
		stub.setCompany(c.getIsCompany())
		stub.setCreated(c.getCreatedOn())
		stub.setEmailAddress(c.getEmail())
		stub.setGivenName(c.getFirstName())
		stub.setFamilyName(c.getLastName())
		stub.setModified(c.getModifiedOn())
		stub.setGender(c.getGender().getDatabaseValue())
		stub.setMarketingViaEmailAllowed(c.getAllowEmail())
		stub.setMarketingViaPostAllowed(c.getAllowPost())
		stub.setMarketingViaSMSAllowed(c.getAllowSms())
		def birthDate = LocalDateUtils.valueToDateAtNoon(c.getBirthDate())
		stub.setDateOfBirth(birthDate)
		stub.setDateOfBirth(c.getDateOfBirth())
		stub.setHomePhoneNumber(c.getHomePhone())
		stub.setBusinessPhoneNumber(c.getWorkPhone())
		stub.setMobilePhoneNumber(c.getMobilePhone())
		if (c.getCountry() != null) {
			stub.setCountryId(c.getCountry().getWillowId())
		}
		if (c.getStudent() != null) {
			stub.setStudentId(c.getStudent().getId())
		}
		if (c.getTutor() != null) {
			stub.setTutorId(c.getTutor().getId())
		}
		stub.setPostcode(c.getPostcode())
		stub.setSuburb(c.getSuburb())
		stub.setState(c.getState())
		stub.setStreet(c.getStreet())
		stub.setUniqueCode(c.getUniqueCode())
		stub.setTaxFileNumber(c.getTfn())
		stub.setMiddleName(c.getMiddleName())
		stub.setAbn(c.getAbn())
		stub.setInvoiceTerms(c.getInvoiceTerms())
		if(c.getTaxOverride() != null) {
			stub.setTaxOverrideId(c.getTaxOverride().getId())
		}
		stub.setTitle(c.getTitle())
		stub.setHonorific(c.getHonorific())
		return stub
	}
}
