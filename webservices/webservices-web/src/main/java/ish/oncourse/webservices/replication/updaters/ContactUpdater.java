package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Country;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.v4.stubs.replication.ContactStub;

import org.apache.cayenne.Cayenne;

public class ContactUpdater extends AbstractWillowUpdater<ContactStub, Contact> {

	@Override
	protected void updateEntity(ContactStub stub, Contact entity, RelationShipCallback callback) {
		entity.setAngelId(stub.getAngelId());
		entity.setBusinessPhoneNumber(stub.getBusinessPhoneNumber());
		entity.setCookieHash(stub.getCookieHash());
		
		if (stub.getStudentId() != null) {
			entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
		}
		
		if (stub.getTutorId() != null) {
			entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class));
		}
		
		
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
