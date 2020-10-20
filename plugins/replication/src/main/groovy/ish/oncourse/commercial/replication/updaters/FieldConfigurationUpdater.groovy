/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.common.types.DeliverySchedule
import ish.common.types.FieldConfigurationType
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.FieldConfiguration
import ish.oncourse.server.cayenne.SurveyFieldConfiguration
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.webservices.v21.stubs.replication.FieldConfigurationStub

/**
 * Created by Artem on 17/11/2016.
 */
class FieldConfigurationUpdater extends AbstractAngelUpdater<FieldConfigurationStub, FieldConfiguration> {
    @Override
    protected void updateEntity(FieldConfigurationStub stub, FieldConfiguration entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setName(stub.getName())
        entity.setIntType(stub.getType())
        entity.setCreatedBy(callback.updateRelationShip(stub.getCreatedBy(), SystemUser.class))
        if (FieldConfigurationType.SURVEY.getDatabaseValue().equals(stub.getType())) {
            def value = TypesUtil.getEnumForDatabaseValue(stub.getDeliverySchedule(), DeliverySchedule.class)
            ((SurveyFieldConfiguration) entity).setDeliverySchedule(value)
        }
    }
}
