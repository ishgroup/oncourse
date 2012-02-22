package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.ContactRelation;
import ish.oncourse.model.ContactRelationType;
import ish.oncourse.webservices.v4.stubs.replication.ContactRelationStub;

public class ContactRelationUpdater extends AbstractWillowUpdater<ContactRelationStub, ContactRelation> {

	@Override
	protected void updateEntity(final ContactRelationStub stub, final ContactRelation entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		Contact fromContact = callback.updateRelationShip(stub.getFromContactId(), Contact.class);
		entity.setFromContact(fromContact);
		entity.setModified(stub.getModified());
		ContactRelationType relationType = callback.updateRelationShip(stub.getTypeId(), ContactRelationType.class);
		entity.setRelationType(relationType);
		Contact toContact = callback.updateRelationShip(stub.getToContactId(), Contact.class);
		entity.setToContact(toContact);
	}

}
