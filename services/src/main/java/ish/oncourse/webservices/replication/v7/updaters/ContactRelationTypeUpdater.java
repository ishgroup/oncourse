package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.ContactRelationType;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.ContactRelationTypeStub;

public class ContactRelationTypeUpdater extends AbstractWillowUpdater<ContactRelationTypeStub, ContactRelationType>{

	@Override
	protected void updateEntity(final ContactRelationTypeStub stub, final ContactRelationType entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setFromContactName(stub.getFromContactName());
		entity.setModified(stub.getModified());
		entity.setToContactName(stub.getToContactName());
        /**
         * delegatedAccessToContact property for the stub was declared as Integer so we need the odd code.
         * We can not change the implementation because we need to support old colleges.
         * We can adjust it only in V7 replication
         */
        entity.setDelegatedAccessToContact(stub.getDelegatedAccessToContact() != null && stub.getDelegatedAccessToContact() != 0);
	}

}
