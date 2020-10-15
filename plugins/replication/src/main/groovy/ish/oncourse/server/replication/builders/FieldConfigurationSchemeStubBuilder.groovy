package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.webservices.v21.stubs.replication.FieldConfigurationSchemeStub

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
