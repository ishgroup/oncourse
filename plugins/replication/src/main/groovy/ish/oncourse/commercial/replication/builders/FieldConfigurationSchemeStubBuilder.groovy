/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.webservices.v22.stubs.replication.FieldConfigurationSchemeStub

/**
 * Created by Artem on 16/11/2016.
 */
class FieldConfigurationSchemeStubBuilder extends AbstractAngelStubBuilder<FieldConfigurationScheme, FieldConfigurationSchemeStub> {

    @Override
    protected FieldConfigurationSchemeStub createFullStub(FieldConfigurationScheme entity) {
        def stub = new FieldConfigurationSchemeStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setName(entity.getName())
        if (entity.getCreatedBy() != null) {
            stub.setCreatedBy(entity.getCreatedBy().getId())
        }
        return stub
    }
}
