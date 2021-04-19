/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.GradingType
import ish.oncourse.webservices.v23.stubs.replication.GradingTypeStub

class GradingTypeStubBuilder extends AbstractAngelStubBuilder<GradingType, GradingTypeStub> {
    @Override
    protected GradingTypeStub createFullStub(GradingType entity) {
        GradingTypeStub stub = new GradingTypeStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setName(entity.getTypeName())
        stub.setMinValue(entity.getMinValue())
        stub.setMaxValue(entity.getMaxValue())
        stub.setEntryType(entity.getEntryType().getDatabaseValue())
        return stub
    }
}
