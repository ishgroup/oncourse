package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.webservices.v4.stubs.replication.ContactStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

import org.apache.cayenne.Cayenne;

public class ContactUpdater extends AbstractWillowUpdater<ContactStub, Contact> {

	@Override
	protected void updateEntity(ContactStub stub, Contact entity, List<ReplicatedRecord> result) {		
		entity.setAngelId(stub.getAngelId());
		entity.setBusinessPhoneNumber(entity.getBusinessPhoneNumber());
		entity.setCollege(college);
		entity.setCookieHash(stub.getCookieHash());
		
		Long countryId = stub.getCountryId();
		if (countryId != null) {
			Country c = Cayenne.objectForPK(entity.getObjectContext(), Country.class, countryId);
			entity.setCountry(c);
		}
		
		entity.setCreated(stub.getCreated());
		entity.setDateOfBirth(stub.getDateOfBirth());
		entity.setEmailAddress(stub.getEmailAddress());
		entity.setFamilyName(stub.getFamilyName());
		entity.setFaxNumber(stub.getFaxNumber());
		entity.setGivenName(stub.getGivenName());
		entity.setHomePhoneNumber(stub.getHomePhoneNumber());
		entity.setIsCompany(stub.isCompany());
		entity.setIsMale(stub.isMale());
		
		entity.setIsMarketingViaEmailAllowed(stub.isMarketingViaEmailAllowed());
		entity.setIsMarketingViaPostAllowed(stub.isMarketingViaPostAllowed());
		entity.setIsMarketingViaSMSAllowed(stub.isMarketingViaSMSAllowed());
	}
}
