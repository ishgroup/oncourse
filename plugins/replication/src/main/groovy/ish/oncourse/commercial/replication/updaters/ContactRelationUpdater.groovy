/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactRelation
import ish.oncourse.server.cayenne.ContactRelationType
import ish.oncourse.webservices.v22.stubs.replication.ContactRelationStub

class ContactRelationUpdater extends AbstractAngelUpdater<ContactRelationStub, ContactRelation> {

    /**
     * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
     * ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
     */
    @Override
    protected void updateEntity(ContactRelationStub stub, ContactRelation entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setFromContact(callback.updateRelationShip(stub.getFromContactId(), Contact.class))
        entity.setModifiedOn(stub.getModified())
        entity.setRelationType(callback.updateRelationShip(stub.getTypeId(), ContactRelationType.class))
        entity.setToContact(callback.updateRelationShip(stub.getToContactId(), Contact.class))
    }
}
