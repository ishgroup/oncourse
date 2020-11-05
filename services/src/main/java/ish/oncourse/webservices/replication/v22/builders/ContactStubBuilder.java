package ish.oncourse.webservices.replication.v22.builders;

import ish.oncourse.model.Contact;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v22.stubs.replication.ContactStub;
import org.apache.commons.lang3.time.DateUtils;

public class ContactStubBuilder extends
		AbstractWillowStubBuilder<Contact, ContactStub> {

	@Override
	protected ContactStub createFullStub(Contact entity) {
		ContactStub stub = new ContactStub();
		stub.setBusinessPhoneNumber(entity.getBusinessPhoneNumber());
		stub.setCookieHash(entity.getCookieHash());
		if (entity.getCountry() != null) {
			stub.setCountryId(entity.getCountry().getId());
		}
		stub.setCreated(entity.getCreated());
		stub.setDateOfBirth(entity.getDateOfBirth() != null ? DateUtils.setHours(entity.getDateOfBirth(), 12): null);
		stub.setEmailAddress(entity.getEmailAddress());
		stub.setFamilyName(entity.getFamilyName());
		stub.setFaxNumber(entity.getFaxNumber());
		stub.setGivenName(entity.getGivenName());
		stub.setHomePhoneNumber(entity.getHomePhoneNumber());
		stub.setCompany(entity.getIsCompany());
		if (entity.getGender()!= null) {
			stub.setGender(entity.getGender().getDatabaseValue());
		}
		stub.setMarketingViaEmailAllowed(entity.getIsMarketingViaEmailAllowed());
		stub.setMarketingViaPostAllowed(entity.getIsMarketingViaPostAllowed());
		stub.setMarketingViaSMSAllowed(entity.getIsMarketingViaSMSAllowed());
		stub.setMobilePhoneNumber(entity.getMobilePhoneNumber());
		stub.setModified(entity.getModified());
		stub.setPassword(entity.getPassword());
		stub.setPasswordHash(entity.getPasswordHash());
		stub.setPostcode(entity.getPostcode());
		stub.setState(entity.getState());
		stub.setStreet(entity.getStreet());
		stub.setSuburb(entity.getSuburb());
		stub.setTaxFileNumber(entity.getTaxFileNumber());
		stub.setUniqueCode(entity.getUniqueCode());
		if (entity.getStudent() != null) {
			stub.setStudentId(entity.getStudent().getId());
		}
		if (entity.getTutor() != null) {
			stub.setTutorId(entity.getTutor().getId());
		}
		stub.setTaxFileNumber(entity.getTaxFileNumber());
		stub.setMiddleName(entity.getMiddleName());
		stub.setAbn(entity.getAbn());
		stub.setInvoiceTerms(entity.getInvoiceTerms());
		if (entity.getTaxOverride() != null) {
			stub.setTaxOverrideId(entity.getTaxOverride().getId());
		}
		stub.setTitle(entity.getTitle());
		stub.setHonorific(entity.getHonorific());
		return stub;
	}
}
