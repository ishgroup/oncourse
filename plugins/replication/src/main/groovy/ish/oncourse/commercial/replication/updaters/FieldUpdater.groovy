/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Field
import ish.oncourse.server.cayenne.FieldConfiguration
import ish.oncourse.server.cayenne.FieldHeading
import ish.oncourse.webservices.v22.stubs.replication.FieldStub
import ish.util.FieldConfigurationUtil

/**
 * Created by Artem on 17/11/2016.
 */
class FieldUpdater extends AbstractAngelUpdater<FieldStub, Field> {

    @Override
    protected void updateEntity(FieldStub stub, Field entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setName(stub.getName())
        entity.setDescription(stub.getDescription())

        def fcClass = FieldConfigurationUtil.getClassByType(stub.getConfigurationType())
        entity.setFieldConfiguration(callback.updateRelationShip(stub.getFieldConfigurationId(), fcClass))

        entity.setFieldHeading(callback.updateRelationShip(stub.getFieldHeadingId(), FieldHeading.class))
        entity.setProperty(stub.getProperty())
        entity.setDefaultValue(stub.getDefaultValue())
        entity.setOrder(stub.getOrder())
        entity.setMandatory(stub.isMandatory())
    }
}
