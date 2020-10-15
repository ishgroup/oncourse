package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.FieldConfigurationLink
import ish.oncourse.webservices.v21.stubs.replication.FieldConfigurationLinkStub

class FieldConfigurationLinkStubBuilder extends AbstractAngelStubBuilder<FieldConfigurationLink, FieldConfigurationLinkStub> {

    @Override
    protected FieldConfigurationLinkStub createFullStub(FieldConfigurationLink entity) {
        def stub = new FieldConfigurationLinkStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setSchemeId(entity.getFieldConfigurationScheme().getId())
        stub.setConfigurationId(entity.getFieldConfiguration().getId())
        stub.setConfigurationType(entity.getFieldConfiguration().getType().getDatabaseValue())
        return stub
    }
}
