/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.EntityRelationType
import ish.oncourse.webservices.v23.stubs.replication.EntityRelationTypeStub

class EntityRelationTypeStubBuilder extends AbstractAngelStubBuilder<EntityRelationType, EntityRelationTypeStub> {

    /**
     * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
     */
    @Override
    protected EntityRelationTypeStub createFullStub(final EntityRelationType entity) {
        final def stub = new EntityRelationTypeStub()
        stub.setAngelId(entity.getId())
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setName(entity.getName())
        stub.setToName(entity.getToName())
        stub.setFromName(entity.getFromName())
        stub.setDescription(entity.getDescription())
        stub.setIsShownOnWeb(entity.getIsShownOnWeb())
        stub.setConsiderHistory(entity.getConsiderHistory())
        stub.setShoppingCart(entity.getShoppingCart().getDatabaseValue())
        stub.setDiscountId(entity.getEntityRelationTypeDiscount() != null ? entity.getEntityRelationTypeDiscount().getId() : null)
        return stub
    }
}
