/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.common.types.GradingEntryType
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.GradingType
import ish.oncourse.webservices.v23.stubs.replication.GradingTypeStub

class GradingTypeUpdater extends AbstractAngelUpdater<GradingTypeStub, GradingType> {
    @Override
    protected void updateEntity(GradingTypeStub stub, GradingType entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setTypeName(stub.getName())
        entity.setMinValue(stub.getMinValue())
        entity.setMaxValue(stub.getMaxValue())
        entity.setEntryType(TypesUtil.getEnumForDatabaseValue(stub.getEntryType(), GradingEntryType.class))
    }
}
