/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.FieldConfiguration
import ish.oncourse.server.cayenne.FieldHeading
import ish.oncourse.webservices.v21.stubs.replication.FieldHeadingStub
import ish.util.FieldConfigurationUtil

/**
 * Created by Artem on 17/11/2016.
 */
class FieldHeadingUpdater extends AbstractAngelUpdater<FieldHeadingStub, FieldHeading>  {
    @Override
    protected void updateEntity(FieldHeadingStub stub, FieldHeading entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setName(stub.getName())
        entity.setDescription(stub.getDescription())

        def fcClass = FieldConfigurationUtil.getClassByType(stub.getConfigurationType())
        entity.setFieldConfiguration(callback.updateRelationShip(stub.getFieldConfigurationId(), fcClass))
        entity.setFieldOrder(stub.getOrder())
    }
}
