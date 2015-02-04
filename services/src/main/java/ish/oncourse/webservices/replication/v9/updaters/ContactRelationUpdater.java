package ish.oncourse.webservices.replication.v9.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.ContactRelation;
import ish.oncourse.model.ContactRelationType;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v9.stubs.replication.ContactRelationStub;

public class ContactRelationUpdater extends AbstractWillowUpdater<ContactRelationStub, ContactRelation> {

	@Override
	protected void updateEntity(final ContactRelationStub stub, final ContactRelation entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setFromContact(callback.updateRelationShip(stub.getFromContactId(), Contact.class));
		entity.setModified(stub.getModified());
		entity.setRelationType(callback.updateRelationShip(stub.getTypeId(), ContactRelationType.class));
		entity.setToContact(callback.updateRelationShip(stub.getToContactId(), Contact.class));
	}

}
