package ish.oncourse.server.replication.updaters


import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.webservices.v21.stubs.replication.FieldConfigurationSchemeStub

/**
 * Created by Artem on 17/11/2016.
 */
class FieldConfigurationSchemeUpdater extends AbstractAngelUpdater<FieldConfigurationSchemeStub, FieldConfigurationScheme> {

    @Override
    protected void updateEntity(FieldConfigurationSchemeStub stub, FieldConfigurationScheme entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setName(stub.getName())
        entity.setCreatedBy(callback.updateRelationShip(stub.getCreatedBy(), SystemUser.class))
    }
}
