/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.GradingItem
import ish.oncourse.webservices.v23.stubs.replication.GradingItemStub

class GradingItemStubBuilder extends AbstractAngelStubBuilder<GradingItem, GradingItemStub> {
    @Override
    protected GradingItemStub createFullStub(GradingItem entity) {
        GradingItemStub stub = new GradingItemStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setName(entity.getItemName())
        stub.setLowerBound(entity.getLowerBound())
        stub.setGradingTypeId(entity.getGradingType().getId())
        return stub
    }
}
