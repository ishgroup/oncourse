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
		entity.setBusinessPhoneNumber(stub.getBusinessPhoneNumber());
		entity.setCookieHash(stub.getCookieHash());
		
		if (stub.getStudentId() != null) {
			Student student = callback.updateRelationShip(stub.getStudentId(), Student.class);
			entity.setStudent(student);
		}
		
		if (stub.getTutorId() != null) {
			Tutor tutor = callback.updateRelationShip(stub.getTutorId(), Tutor.class);
			entity.setTutor(tutor);
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
		
		entity.setSuburb(stub.getSuburb());
		entity.setState(stub.getState());
		entity.setStreet(stub.getStreet());
		entity.setPostcode(stub.getPostcode());
		entity.setUniqueCode(stub.getUniqueCode());

		entity.setIsMarketingViaEmailAllowed(stub.isMarketingViaEmailAllowed());
		entity.setIsMarketingViaPostAllowed(stub.isMarketingViaPostAllowed());
		entity.setIsMarketingViaSMSAllowed(stub.isMarketingViaSMSAllowed());
	}
}
