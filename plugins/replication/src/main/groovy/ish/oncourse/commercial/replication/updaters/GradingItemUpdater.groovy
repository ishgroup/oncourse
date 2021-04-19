/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.GradingItem
import ish.oncourse.server.cayenne.GradingType
import ish.oncourse.webservices.v23.stubs.replication.GradingItemStub

class GradingItemUpdater extends AbstractAngelUpdater<GradingItemStub, GradingItem> {
    @Override
    protected void updateEntity(GradingItemStub stub, GradingItem entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setItemName(stub.getName())
        entity.setLowerBound(stub.getLowerBound())
        entity.setGradingType(callback.updateRelationShip(stub.getGradingTypeId(), GradingType.class))
        entity
    }
}
