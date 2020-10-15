/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.replication.builders

import ish.common.types.FieldConfigurationType
import ish.oncourse.server.cayenne.FieldConfiguration
import ish.oncourse.server.cayenne.SurveyFieldConfiguration
import ish.oncourse.webservices.v21.stubs.replication.FieldConfigurationStub

/**
 * Created by Artem on 16/11/2016.
 */
class FieldConfigurationStubBuilder extends AbstractAngelStubBuilder<FieldConfiguration, FieldConfigurationStub> {
    @Override
    protected FieldConfigurationStub createFullStub(FieldConfiguration entity) {
        def stub = new FieldConfigurationStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setName(entity.getName())
        stub.setType(entity.getType().getDatabaseValue())
        if (entity.getCreatedBy() != null) {
            stub.setCreatedBy(entity.getCreatedBy().getId())
        }
        if (FieldConfigurationType.SURVEY.equals(entity.getType())) {
            stub.setDeliverySchedule(((SurveyFieldConfiguration) entity).getDeliverySchedule().getDatabaseValue())
        }
        return stub
    }
}
