package ish.oncourse.webservices.builders.replication;

import java.util.Map;

import ish.oncourse.model.Contact;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ContactStub;

public class ContactStubBuilder extends AbstractWillowStubBuilder<Contact, ContactStub> {
	
	public ContactStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowStubBuilder next) {
		super(queue, next);
	}

	@Override
	protected ContactStub createFullStub(Contact entity) {
		ContactStub stub = new ContactStub();
		
		stub.setAngelId(entity.getAngelId());
		stub.setBusinessPhoneNumber(entity.getBusinessPhoneNumber());
		stub.setCookieHash(entity.getCookieHash());
		stub.setCountryId(entity.getCountryId());
		stub.setCreated(entity.getCreated());
		stub.setDateOfBirth(entity.getDateOfBirth());
		stub.setEmailAddress(entity.getEmailAddress());
		stub.setFamilyName(entity.getFamilyName());
		stub.setFaxNumber(entity.getFaxNumber());
		stub.setGivenName(entity.getGivenName());
		stub.setHomePhoneNumber(entity.getHomePhoneNumber());
		stub.setIsCompany(entity.getIsCompany());
		stub.setIsMale(entity.getIsMale());
		stub.setIsMarketingViaEmailAllowed(entity.getIsMarketingViaEmailAllowed());
		stub.setIsMarketingViaPostAllowed(entity.getIsMarketingViaPostAllowed());
		stub.setIsMarketingViaSMSAllowed(entity.getIsMarketingViaSMSAllowed());
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
		stub.setWillowId(entity.getId());
		
		if (entity.getStudent() != null) {
			stub.setStudent(findRelatedStub(entity.getStudent()));
		}
		else {
			stub.setTutor(findRelatedStub(entity.getTutor()));
		}
		
		return stub;
	}
}
