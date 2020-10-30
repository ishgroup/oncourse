/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.FieldConfiguration
import ish.oncourse.server.cayenne.FieldConfigurationLink
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.webservices.v22.stubs.replication.FieldConfigurationLinkStub
import ish.util.FieldConfigurationUtil

class FieldConfigurationLinkUpdater extends AbstractAngelUpdater<FieldConfigurationLinkStub, FieldConfigurationLink> {

    @Override
    protected void updateEntity(FieldConfigurationLinkStub stub, FieldConfigurationLink entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setFieldConfigurationScheme(callback.updateRelationShip(stub.getSchemeId(), FieldConfigurationScheme.class))

        def fcClass = FieldConfigurationUtil.getClassByType(stub.getConfigurationType())
        entity.setFieldConfiguration(callback.updateRelationShip(stub.getConfigurationId(), fcClass))
    }
}
