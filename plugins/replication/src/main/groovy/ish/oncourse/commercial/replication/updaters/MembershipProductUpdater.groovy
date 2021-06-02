/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.common.types.ExpiryType
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.webservices.v23.stubs.replication.MembershipProductStub

/**
 */
class MembershipProductUpdater extends AbstractProductUpdater<MembershipProductStub, MembershipProduct> {

    @Override
    protected void updateEntity(MembershipProductStub stub, MembershipProduct entity, RelationShipCallback callback) {
        super.updateEntity(stub, entity, callback)

        entity.setExpiryDays(stub.getExpiryDays())
        if (stub.getExpiryType()) {
            entity.setExpiryType(TypesUtil.getEnumForDatabaseValue(stub.getExpiryType(), ExpiryType.class))
        }
    }
}
