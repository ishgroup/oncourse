package ish.oncourse.webservices.replication.v19.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Tax;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v19.stubs.replication.ContactStub;
import org.apache.cayenne.Cayenne;

public class ContactUpdater extends AbstractWillowUpdater<ContactStub, Contact> {

	@Override
	protected void updateEntity(ContactStub stub, Contact entity, RelationShipCallback callback) {
		entity.setBusinessPhoneNumber(stub.getBusinessPhoneNumber());
		entity.setCookieHash(stub.getCookieHash());
		if (stub.getCountryId() != null) {
			entity.setCountry(Cayenne.objectForPK(entity.getObjectContext(), Country.class, stub.getCountryId()));
		}
		entity.setCreated(stub.getCreated());
		entity.setDateOfBirth(stub.getDateOfBirth());
		entity.setEmailAddress(stub.getEmailAddress());
		entity.setFamilyName(stub.getFamilyName());
		entity.setFaxNumber(stub.getFaxNumber());
		entity.setGivenName(stub.getGivenName());
		entity.setHomePhoneNumber(stub.getHomePhoneNumber());
		entity.setMobilePhoneNumber(stub.getMobilePhoneNumber());
		entity.setIsCompany(stub.isCompany());
		entity.setIsMale(stub.isMale());
		entity.setSuburb(stub.getSuburb());
		entity.setState(stub.getState());
		entity.setStreet(stub.getStreet());
		entity.setPostcode(stub.getPostcode());
		entity.setUniqueCode(stub.getUniqueCode());
		entity.setIsMarketingViaEmailAllowed(stub.isMarketingViaEmailAllowed());
		entity.setIsMarketingViaPostAllowed(stub.isMarketingViaPostAllowed());
		entity.setIsMarketingViaSMSAllowed(stub.isMarketingViaSMSAllowed());
		entity.setTaxFileNumber(stub.getTaxFileNumber());
		entity.setMiddleName(stub.getMiddleName());
		entity.setAbn(stub.getAbn());
		entity.setInvoiceTerms(stub.getInvoiceTerms());
		entity.setTitle(stub.getTitle());
		entity.setHonorific(stub.getHonorific());
		entity.setTaxOverride(callback.updateRelationShip(stub.getTaxOverrideId(), Tax.class));
	}
}
