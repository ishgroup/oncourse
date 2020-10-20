/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Field
import ish.oncourse.webservices.v21.stubs.replication.FieldStub

/**
 * Created by Artem on 16/11/2016.
 */
class FieldStubBuilder extends AbstractAngelStubBuilder<Field, FieldStub>{
    @Override
    protected FieldStub createFullStub(Field entity) {
        def stub = new FieldStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setName(entity.getName())
        stub.setDescription(entity.getDescription())
        stub.setFieldConfigurationId(entity.getFieldConfiguration().getId())
        stub.setConfigurationType(entity.getFieldConfiguration().getType().getDatabaseValue())
        if (entity.getFieldHeading() != null) {
            stub.setFieldHeadingId(entity.getFieldHeading().getId())
        }
        stub.setMandatory(entity.getMandatory())
        stub.setOrder(entity.getOrder())
        stub.setProperty(entity.getProperty())
        stub.setDefaultValue(entity.getDefaultValue())
        return stub
    }
}
