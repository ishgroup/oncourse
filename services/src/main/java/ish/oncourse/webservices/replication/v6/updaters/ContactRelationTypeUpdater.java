package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.ContactRelationType;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.ContactRelationTypeStub;

public class ContactRelationTypeUpdater extends AbstractWillowUpdater<ContactRelationTypeStub, ContactRelationType>{

	@Override
	protected void updateEntity(final ContactRelationTypeStub stub, final ContactRelationType entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setFromContactName(stub.getFromContactName());
		entity.setModified(stub.getModified());
		entity.setToContactName(stub.getToContactName());
	}

}
